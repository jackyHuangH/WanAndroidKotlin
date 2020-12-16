package com.jacky.wanandroidkotlin.wrapper.musicplay

import android.media.MediaPlayer
import android.util.Log
import com.hjq.toast.ToastUtils

/**
 * @author:Hzj
 * @date  :2020/12/14
 * desc  ：基于MediaPlayer播放音乐
 * record：
 */
class MediaPlayerHelper : IPlayer,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private val mediaPlayer by lazy { MediaPlayer() }
    private var iPlayerStatus: IPlayerStatus? = null

    companion object {
        private const val TAG = "MediaPlayerHelper"
    }

    init {
        mediaPlayer.apply {
            //播放器准备完成监听
            setOnPreparedListener(this@MediaPlayerHelper)
            //播放完成监听
            setOnCompletionListener(this@MediaPlayerHelper)
            //缓冲更新监听
            setOnBufferingUpdateListener(this@MediaPlayerHelper)
            //播放错误监听
            setOnErrorListener(this@MediaPlayerHelper)
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        //准备完毕直接播放
        mediaPlayer.start()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        iPlayerStatus?.onPlayComplete()
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        iPlayerStatus?.onBufferingUpdate(percent)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean = true

    override fun setPlayStatus(iPlayerStatus: IPlayerStatus) {
        this.iPlayerStatus = iPlayerStatus
    }

    override fun play(audioPath: String) {
        //从头播放歌曲
        mediaPlayer.reset()
        kotlin.runCatching {
            //设置播放源，可能找不到文件
            mediaPlayer.setDataSource(audioPath)
        }.onSuccess {
            //找到文件，开始准备
            mediaPlayer.prepare()
        }.onFailure {
            //找不到文件，提示错误
            Log.e(TAG, "${it.printStackTrace()}")
            ToastUtils.show("无效的音频文件！")
        }
    }

    override fun stop() {
        mediaPlayer.stop()
    }

    override fun resume() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun seekToPlay(duration: Int) {
        mediaPlayer.seekTo(duration)
    }

    override fun reset() {
        mediaPlayer.reset()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun getProgress(): Int = mediaPlayer.currentPosition
}

/**
 * 播放器功能接口
 */
interface IPlayer {
    //设置状态监听回调
    fun setPlayStatus(iPlayerStatus: IPlayerStatus)

    //播放新的音频
    fun play(audioPath: String)

    //停止播放
    fun stop()

    //恢复播放
    fun resume()

    //暂停播放
    fun pause()

    //跳转播放
    fun seekToPlay(duration: Int)

    //重置
    fun reset()

    //释放资源
    fun release()

    //是否正在播放
    fun isPlaying(): Boolean

    //获取播放进度
    fun getProgress(): Int
}