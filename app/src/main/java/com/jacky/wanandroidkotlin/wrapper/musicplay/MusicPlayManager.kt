package com.jacky.wanandroidkotlin.wrapper.musicplay

import android.content.Context
import android.os.CountDownTimer

/**
 * @author:Hzj
 * @date  :2020/12/12
 * desc  ：音乐播放管理工具
 * record：
 */
object MusicPlayManager : IPlayerStatus {

    /**
     * 播放状态，默认为重置
     */
    private var playStatus = PlayerStatus.PLAY_RESET

    /**
     * 音乐观察者集合,目前有三个
     * 1.播放界面
     * 2.悬浮窗
     * 3.通知栏
     */
    private val observers = mutableListOf<AudioObserver>()
    private var mCountDownTimer: CountDownTimer? = null

    private lateinit var mPlayListManager: PlayListManager
    private val playerHelper: IPlayer by lazy { MediaPlayerHelper() }

    //初始化播放器
    fun initPlayer(context: Context) {
        mPlayListManager = PlayListManager.instance
        playerHelper.setPlayStatus(this)
        startTimer()
    }

    /**
     * 开始计时，每1000ms更新一次进度条
     */
    private fun startTimer() {
        mCountDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                //仅在播放状态通知观察者更新进度条
                if (playerHelper.isPlaying()) {
                    sendProgressToObserver(playerHelper.getProgress())
                }
            }

            override fun onFinish() {
            }
        }.apply { start() }
    }

    /**
     * 通知观察者更新进度
     */
    private fun sendProgressToObserver(currDuration: Int) {
        observers.forEach { observer ->
            mPlayListManager.getCurrentAudioBean()?.duration?.let { totalDuration ->
                observer.onProgress(currDuration, totalDuration)
            }
        }
    }

    /**
     * 注册观察者
     */
    fun register(audioObserver: AudioObserver) {
        if (observers.contains(audioObserver)) {
            observers.remove(audioObserver)
        }
        observers.add(audioObserver)
        //注册时主动触发一次更新观察者，粘性通知效果
        notifyObserver(audioObserver)
    }

    /**
     * 注销观察者
     */
    fun unregister(audioObserver: AudioObserver) {
        if (observers.contains(audioObserver)) {
            observers.remove(audioObserver)
        }
    }

    /**
     * 手动更新观察者
     */
    private fun notifyObserver(audioObserver: AudioObserver) {
        mPlayListManager.getCurrentAudioBean()?.let {
            audioObserver.onAudioBean(it)
            audioObserver.onProgress(playerHelper.getProgress(), it.duration)
        }
        audioObserver.apply {
            onPlayMode(mPlayListManager.getPlayMode())
            onPlayerStatus(playStatus)
        }
    }

    /**
     * 切换播放模式
     */
    fun switchPlayMode() {
        sendPlayModeToObserver(mPlayListManager.switchPlayMode())
    }

    /**
     * 播放暂停控制
     */
    fun playOrPause() {
        if (mPlayListManager.getCurrentAudioBean() == null) {
            //第一次进入播放
            startPlay()
        } else {
            if (playerHelper.isPlaying()) {
                pause()
            } else {
                resume()
            }
        }
    }

    /**
     * 第一次进入,播放器未被初始化,默认播放第一个
     */
    fun startPlay() {
        playNewAudio(mPlayListManager.startPlayAudio())
    }

    //暂停
    private fun pause() {
        playStatus = PlayerStatus.PLAY_PAUSE
        playerHelper.pause()
        sendPlayStatusToObserver()
    }

    //恢复播放
    private fun resume() {
        playStatus = PlayerStatus.PLAY_RESUME
        playerHelper.resume()
        sendPlayStatusToObserver()
    }

    /**
     *获取当前播放曲目列表
     */
    fun getCurrentAudioList(): MutableList<AudioBean> = mPlayListManager.getCurrentAudioList()

    /**
     * 播放一个新的音频
     */
    fun playNewAudio(audioBean: AudioBean?) {
        if (audioBean == null) {
            //音频文件无效，重置
            playerHelper.reset()
            sendResetToObserver()
        } else {
            playStatus = PlayerStatus.PLAY_START
            mPlayListManager.setCurrentAudioBean(audioBean)
            audioBean.path?.let { playerHelper.play(it) }
            sendAudioBeanToObserver(audioBean)
            sendPlayStatusToObserver()
        }
    }

    /**
     * 播放下一首
     */
    fun nextAudio() {
        playNewAudio(mPlayListManager.nextAudio())
    }

    /**
     * 播放上一首
     */
    fun previousAudio() {
        playNewAudio(mPlayListManager.previousAudio())
    }

    /**
     * 跳转播放
     */
    fun seekToPlay(duration: Int) {
        playerHelper.seekToPlay(duration)
    }

    private fun sendPlayModeToObserver(switchPlayMode: Int) {
        observers.forEach { observer ->
            observer.onPlayMode(switchPlayMode)
        }
    }

    private fun sendResetToObserver() {
        observers.forEach { it.onReset() }
    }

    private fun sendPlayStatusToObserver() {
        observers.forEach { it.onPlayerStatus(playStatus) }
    }

    private fun sendAudioBeanToObserver(audioBean: AudioBean) {
        observers.forEach { it.onAudioBean(audioBean) }
    }

    override fun onBufferingUpdate(percent: Int) {
        //缓冲进度回调
    }

    override fun onPlayComplete() {
        //当前播放完成，播放下一首
        nextAudio()
    }

    /**
     * 释放资源
     */
    fun release() {
        mCountDownTimer?.cancel()
        mCountDownTimer = null
        mPlayListManager.clear()
        playerHelper.reset()
        playerHelper.release()
    }
}

//播放器状态，当前有4种
// 如果整型字面量的值在-128到127之间，那么不会new新的Integer对象，而是直接引用常量池中的Integer对象，
// 所以playStatus设置大于127就会自动装箱成Integer，对应的引用也就不同，达到更新目的。
object PlayerStatus {
    /**
     * 重置
     */
    const val PLAY_RESET = 100

    /**
     * 从头播放
     */
    const val PLAY_START = 200

    /**
     * 恢复播放
     */
    const val PLAY_RESUME = 300

    /**
     * 暂停播放
     */
    const val PLAY_PAUSE = 400
}