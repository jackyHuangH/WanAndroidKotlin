@file:Suppress("DEPRECATION")

package com.jacky.wanandroidkotlin.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.Choreographer
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.jacky.wanandroidkotlin.wrapper.safelyRun
import java.io.File
import kotlin.math.roundToInt

fun Context.getVersionName(packageName: String = this.packageName): String? =
    getPackageInfo(packageName)?.versionName


fun Context.getVersionCode(packageName: String = this.packageName): Long? {
    return this.getPackageInfo(packageName)?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            it.longVersionCode
        } else {
            it.versionCode.toLong()
        }
    }
}

fun Context.isPackageExists(packageName: String): Boolean {
    packageManager.getInstalledPackages(0).forEach {
        if (it.packageName == packageName) return true
    }
    return false
}

fun Context.isSystemApp(packageName: String): Boolean = getApplicationInfo(packageName)?.let {
    it.flags and ApplicationInfo.FLAG_SYSTEM > 0
} ?: false


@SuppressLint("NewApi")
fun Context.isTopActivity(packageName: String = this.packageName): Boolean {
    return (this.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.getRunningTasks(1)
        ?.let {
            it[0].topActivity?.packageName == packageName
        } ?: false
}

fun Context.getPackageInfo(packageName: String = this.packageName): PackageInfo? {
    return try {
        packageManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Context.getApplicationInfo(packageName: String = this.packageName): ApplicationInfo? {
    return this.getPackageInfo(packageName)?.applicationInfo
}

fun Context.getMetaData(key: String, packageName: String = this.packageName): String? {
    return safelyRun {
        packageManager.getApplicationInfo(
            packageName,
            PackageManager.GET_META_DATA
        ).metaData?.getString(key)
    }
}

//fun Context.getMetaData(
//        key: String,
//        defaultValue: String,
//        packageName: String = this.packageName
//): String {
//    return safelyRun {
//        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData?.getString(key)
//    } ?: defaultValue
//}

/**
 * 屏幕刷新率：
 *  屏幕刷新率代表屏幕在一秒内刷新屏幕的次数，这个值用赫兹来表示，取决于硬件的固定参数。这个值一般是60Hz,即每16.66ms系统发出一个 VSYNC 信号来通知刷新一次屏幕。
 *
 *  帧速率：
 *  帧速率代表了GPU在一秒内绘制操作的帧数，比如30fps/60fps。
 */
object RateUtil {
    private var mLastFrameTime: Long = 0L
    private var mFrameCount: Int = 0

    /**
     * 检测当前屏幕刷新率
     */
    fun detectRefreshRate() {
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {

            override fun doFrame(frameTimeNanos: Long) {
                if (mLastFrameTime == 0L) {
                    mLastFrameTime = frameTimeNanos;
                }
                //得到毫秒，正常是 16.66 ms
                val diff = (frameTimeNanos - mLastFrameTime) / 1000000.0f
                if (diff > 500) {
                    val fps: Double = (((mFrameCount * 1000L)) / diff).toDouble()
                    mFrameCount = 0;
                    mLastFrameTime = 0;
                    Log.d("doFrame", "doFrame: " + fps);
                } else {
                    ++mFrameCount;
                }
                Choreographer.getInstance().postFrameCallback(this)
            }
        });
    }
}


object KeyboardUtils {

    /**
     * 动态显示软键盘
     *
     * @param editText
     */
    fun showSoftInput(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val context = editText.context
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, 0)
    }

    /**
     * 动态隐藏软键盘
     *
     * @param activity
     */
    fun hideSoftInput(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }
}

object DisplayUtils {

    /**
     * 获取DisplayMetrics对象
     *
     * @return
     */
    private val displayMetrics: DisplayMetrics by lazy { Resources.getSystem().displayMetrics }

    /**
     * 获取屏幕的宽度（像素）
     *
     * @return
     */
    fun screenWidth(): Int = displayMetrics.widthPixels

    /**
     * 获取屏幕的高（像素）
     *
     * @return
     */
    fun screenHeight(): Int = displayMetrics.heightPixels

    /**
     * dp转px，保证尺寸大小不变
     *
     * @param dpValue
     * @return
     */
    fun dp2px(dpValue: Number): Int = (dpValue.toFloat() * displayMetrics.density).roundToInt()


    /**
     * px转dp，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    fun px2dp(pxValue: Number): Int = (pxValue.toFloat() / displayMetrics.density).roundToInt()

    /**
     * px转sp，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    fun px2sp(pxValue: Number): Int =
        (pxValue.toFloat() / displayMetrics.scaledDensity).roundToInt()

    /**
     * sp转px，保证尺寸大小不变
     *
     * @param spValue
     * @return
     */
    fun sp2px(spValue: Number): Int =
        (spValue.toFloat() * displayMetrics.scaledDensity).roundToInt()

}

object ResolveUtils {

    /**
     * 通过id获取专辑图片uri
     */
    fun albumUriById(albumId: Long): Uri {
        return ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )
    }

    fun resolveString(
        context: Context,
        @StringRes res: Int? = null,
        @StringRes fallback: Int? = null,
        html: Boolean = false
    ): CharSequence? {
        val resourceId = res ?: (fallback ?: 0)
        if (resourceId == 0) return null
        val text = context.resources.getText(resourceId)
        if (html) {
            @Suppress("DEPRECATION")
            return Html.fromHtml(text.toString())
        }
        return text
    }

    fun resolveDrawable(
        context: Context,
        @DrawableRes res: Int? = null,
        @AttrRes attr: Int? = null,
        @DrawableRes fallback: Int? = null
    ): Drawable? {
        if (attr != null) {
            val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
            try {
                val d = a.getDrawable(0)
                if (d != null) return d
            } finally {
                a.recycle()
            }
        }
        return (res ?: fallback)?.let { ContextCompat.getDrawable(context, it) }
    }

    @ColorInt
    fun resolveColor(
        context: Context,
        @ColorRes res: Int? = null,
        @AttrRes attr: Int? = null,
        fallback: (() -> Int)? = null
    ): Int {
        if (attr != null) {
            val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
            try {
                val result = a.getColor(0, 0)
                if (result == 0 && fallback != null) {
                    return fallback()
                }
                return result
            } finally {
                a.recycle()
            }
        }
        return ContextCompat.getColor(context, res ?: 0)
    }

    fun resolveColors(
        context: Context,
        attrs: IntArray,
        fallback: ((forAttr: Int) -> Int)? = null
    ): IntArray {
        val a = context.theme.obtainStyledAttributes(attrs)
        try {
            return (attrs.indices).map { index ->
                val color = a.getColor(index, 0)
                return@map if (color != 0) {
                    color
                } else {
                    fallback?.invoke(attrs[index]) ?: 0
                }
            }
                .toIntArray()
        } finally {
            a.recycle()
        }
    }

    fun resolveInt(
        context: Context,
        @AttrRes attr: Int,
        defaultValue: Int
    ): Int {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            return a.getInt(0, defaultValue)
        } finally {
            a.recycle()
        }
    }
}

object ClipboardUtils {

    fun getClipText(context: Context): CharSequence? {
        return (context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.run {
            primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.text
        }
    }
}

object UriUtils {

    fun getFilePathByUri(context: Context, uri: Uri): String? {
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    return when (type) {
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        else -> null
                    }?.let { contentUri ->
                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])
                        getDataColumn(context, contentUri, selection, selectionArgs)
                    }
                }
            }
        } else {
            // 以 file:// 开头的
            if (ContentResolver.SCHEME_FILE == uri.scheme) {
                return uri.path
            }
            // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
            if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
                val cursor = context.contentResolver.query(
                    uri,
                    arrayOf(MediaStore.Images.Media.DATA),
                    null,
                    null,
                    null
                )
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        if (columnIndex > -1) {
                            cursor.close()
                            return cursor.getString(columnIndex)
                        }
                    }
                    cursor.close()
                }
            }
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        return try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                cursor.getString(index)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            cursor?.close()
        }

    }

    private fun isExternalStorageDocument(uri: Uri): Boolean =
        "com.android.externalstorage.documents" == uri.authority

    private fun isDownloadsDocument(uri: Uri): Boolean =
        "com.android.providers.downloads.documents" == uri.authority

    private fun isMediaDocument(uri: Uri): Boolean =
        "com.android.providers.media.documents" == uri.authority

}

//硬盘缓存的文件夹
internal const val DEFAULT_CACHE_DIR = " /zenchn/tds"

fun Context.getDiskCacheDir(): String =
    (if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
        externalCacheDir?.path
    } else null) ?: cacheDir.path
    ?: (Environment.getExternalStorageDirectory().path + DEFAULT_CACHE_DIR)

/**
 * 获取（创建）文件的文件夹
 *
 * @param uniqueNameDir
 * @return
 */
fun Context.openDiskCacheDir(uniqueNameDir: String): String {
    return File(getDiskCacheDir(), uniqueNameDir).apply {
        if (exists()) {
            if (isFile) {
                delete()
                mkdirs()
            }
        } else {
            mkdirs()
        }
    }.absolutePath

}
