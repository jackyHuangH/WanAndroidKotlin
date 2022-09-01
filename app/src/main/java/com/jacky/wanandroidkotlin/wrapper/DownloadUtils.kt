package com.jacky.wanandroidkotlin.wrapper

import android.os.Environment
import android.util.Log
import com.hjq.toast.ToastUtils
import com.jacky.wanandroidkotlin.model.entity.GirlEntity
import com.jacky.support.utils.FileIOUtils
import com.jacky.support.utils.LoggerKit
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 * @author:Hzj
 * @date  :2021/10/29
 * desc  ：
 * record：
 */
object DownloadUtils {
    private const val TAG = "DownloadUtils"

    @JvmField
    val DOWNLOAD_FILE_DIR_PATH =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/WanAndroid/"

    private val okHttpClient by lazy { OkHttpClient() }

    private fun checkDir(filePath: String) {
        //初始化文件下载目录,不存在就创建目录
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            FileIOUtils.createOrExistsDir(File(filePath))
        }
    }

    //shp文件存放总目录
    fun getShpFileRootDirPath(): String {
        val dirPath = DOWNLOAD_FILE_DIR_PATH
        checkDir(dirPath)
        return dirPath
    }

    fun download(
        fileInfo: GirlEntity,
        onDownloadSuccess: ((File) -> Unit)? = null,
        onDownloadProgress: ((Int) -> Unit)? = null,
        onDownloadFail: ((Exception) -> Unit)? = null
    ) {
        val downloadFilePath = getShpFileRootDirPath()
        val destFileName = "${fileInfo.author}.jpg"
        download(
            fileInfo.download_url,
            downloadFilePath,
            destFileName,
            onDownloadSuccess,
            onDownloadProgress,
            onDownloadFail
        )
    }


    /**
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称，后面记得拼接后缀，否则手机没法识别文件类型
     * @param listener     下载监听
     */
    fun download(
        url: String,
        destFileDir: String,
        destFileName: String,
        onDownloadSuccess: ((File) -> Unit)? = null,
        onDownloadProgress: ((Int) -> Unit)? = null,
        onDownloadFail: ((Exception) -> Unit)? = null
    ) {
        val request: Request = Request.Builder().url(url).build()

        ToastUtils.show("开始下载：$destFileName")

        //异步请求
        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                // 下载失败监听回调
                Log.e(TAG,e.message.orEmpty())
                onDownloadFail?.invoke(e)
            }

            override fun onResponse(call: Call, response: Response) {
                var ins: InputStream? = null
                val buf = ByteArray(2048)
                var len = 0
                var fos: FileOutputStream? = null

                //储存下载文件的目录
                val dir = File(destFileDir)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val file = File(dir, destFileName)
                try {
                    ins = response.body?.byteStream()
                    val total: Long = response.body?.contentLength() ?: 0L
                    fos = FileOutputStream(file)
                    var sum: Long = 0
                    var mTempProgress = 0
                    while (ins?.read(buf)?.also { len = it } != -1) {
                        fos.write(buf, 0, len)
                        sum += len.toLong()
                        val progress = (sum * 1.0f / total * 100).toInt()
                        if (progress - mTempProgress >= 1) {
                            //下载中更新进度条
                            onDownloadProgress?.invoke(progress)
                            Log.d(TAG,"downloading progress:$progress")
                            mTempProgress=progress
                        }
                    }
                    fos.flush()
                    //下载完成
                    onDownloadSuccess?.invoke(file)
                    Log.d(TAG,"downloading success:$file")
                } catch (e: Exception) {
                    Log.e(TAG,e.message.orEmpty())
                    onDownloadFail?.invoke(e)
                } finally {
                    try {
                        ins?.close()
                        fos?.close()
                    } catch (e: IOException) {
                        LoggerKit.e(e)
                    }
                }
            }
        })
    }

}