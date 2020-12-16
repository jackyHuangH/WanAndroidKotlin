package com.jacky.wanandroidkotlin.wrapper.musicplay

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

/**
 * @author:Hzj
 * @date  :2020/12/14
 * desc  ：音乐文件仓库
 * record：
 */

/**
 * 使用contentProvider读取手机音乐文件
 */
@SuppressLint("Recycle", "InlinedApi")
fun Context.readLocalMusicList(): MutableList<AudioBean> {
    val audioList = mutableListOf<AudioBean>()
    val cursor: Cursor? = contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        null, null, null,
        MediaStore.Audio.Media.DEFAULT_SORT_ORDER
    )
    cursor?.let {
        var audioBean: AudioBean
        while (cursor.moveToNext()) {
            audioBean = AudioBean().apply {
                id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
                albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                //设置列表类型
                playListType = PlayListType.LOCAL_PLAY_LIST
            }
            //过滤时长小于1分钟的音频
            if (audioBean.duration > 60000) {
                audioList.add(audioBean)
            }
        }
        //关闭cursor，避免内存泄漏
        it.close()
    }
    return audioList
}