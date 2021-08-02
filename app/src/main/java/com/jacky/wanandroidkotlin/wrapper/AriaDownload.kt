package com.jacky.wanandroidkotlin.wrapper

import android.content.Context
import android.os.Environment
import android.util.Log
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.task.DownloadTask
import com.arialyy.aria.util.CommonUtil
import com.hjq.toast.ToastUtils
import com.jacky.wanandroidkotlin.model.entity.GirlEntity
import com.zenchn.support.utils.FileIOUtils
import java.io.File


/**
 * @author:Hzj
 * @date  :2021/7/30
 * desc  ：Aria 文件下载工具封装
 * record：
 */
object AriaDownload {
    private const val TAG = "AriaDownload"

    @JvmField
    val DOWNLOAD_FILE_PATH =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/WanAndroid/"

    private val mTaskIdSet: MutableSet<Long> = mutableSetOf()

    private var mDownloadPercentCallback: ((Int) -> Unit)? = null

    init {
        //注册Aria
        Aria.download(this).register()
        checkDir()
    }

    private fun checkDir(){
        //初始化文件下载目录,不存在就创建目录
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            FileIOUtils.createOrExistsDir(File(DOWNLOAD_FILE_PATH))
        }
    }

    fun downloadFile(girlEntity: GirlEntity, callback: ((Int) -> Unit)? = null): Long {
        checkDir()
        this.mDownloadPercentCallback = callback
        val filePath = "$DOWNLOAD_FILE_PATH${girlEntity.desc}.jpg"
        val taskId = Aria.download(this)
            .load(girlEntity.url) //读取下载地址
            .setFilePath(filePath) //设置文件保存的完整路径
            .resetState()
            .create() //创建并启动下载
        mTaskIdSet.add(taskId)
        return taskId
    }

    @Download.onWait
    fun onWait(task: DownloadTask) {
        Log.d(TAG, "wait ==> " + task.downloadEntity.fileName)
    }

    @Download.onPre
    fun onPre(task: DownloadTask?) {
        Log.d(TAG, "onPre")
    }

    @Download.onTaskStart
    fun taskStart(task: DownloadTask?) {
        Log.d(TAG, "onStart")
        ToastUtils.show("开始下载")
    }

    @Download.onTaskRunning
    fun running(task: DownloadTask) {
        val p: Int = task.percent //任务进度百分比
        val speed: Long = task.speed //原始byte长度速度
        val convertSpeed = task.convertSpeed //转换单位后的下载速度，单位转换需要在配置文件中打开
        mDownloadPercentCallback?.invoke(p)
        Log.d(TAG, "downloading:$p--speed:$speed--prettySpeed:$convertSpeed")
    }

    @Download.onTaskResume
    fun taskResume(task: DownloadTask?) {
        Log.d(TAG, "resume")
        ToastUtils.show("恢复下载")
    }

    @Download.onTaskStop
    fun taskStop(task: DownloadTask?) {
        Log.d(TAG, "stop")
        ToastUtils.show("暂停下载")
    }

    @Download.onTaskCancel
    fun taskCancel(task: DownloadTask?) {
        Log.d(TAG, "cancel")
    }

    @Download.onTaskFail
    fun taskFail(task: DownloadTask?) {
        Log.d(TAG, "fail")
        ToastUtils.show("下载失败!")
    }

    @Download.onTaskComplete
    fun taskComplete(task: DownloadTask) {
        val downloadRealUrl = task.downloadEntity.realUrl
        val filePath = task.downloadEntity.filePath
        Log.d(TAG, "downloadRealUrl ==> $downloadRealUrl")
        Log.d(TAG, "md5Code ==> " + CommonUtil.getFileMD5(File(filePath)))
        ToastUtils.show("下载完成，文件已保存:$filePath")
    }


    fun stopDownloadTask(taskId: Long) {
        Aria.download(this).load(taskId).stop()
    }

    fun resumeDownloadTask(taskId: Long) {
        Aria.download(this).load(taskId).resume()
    }

    fun cancelDownloadTask(taskId: Long) {
        Aria.download(this).load(taskId).cancel()
    }

    /**
     * 取消下载任务并注销Aria,务必在界面销毁时调用此方法！！！
     */
    fun clearDownloadTask(context: Context) {
        if (mTaskIdSet.isNotEmpty()) {
            mTaskIdSet.forEach {
                cancelDownloadTask(it)
            }
        }
        //注销Aria
        Aria.download(context).unRegister()
    }
}