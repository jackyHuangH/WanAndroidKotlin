package com.jacky.wanandroidkotlin.wrapper.musicplay

/**
 * @author:Hzj
 * @date  :2020/12/15
 * desc  ：音频播放观察者,可实时观察到音频信息、播放状态、播放进度
 * record：
 */
interface AudioObserver {
    /**
     * 歌曲信息
     */
    fun onAudioBean(audioBean: AudioBean)

    /**
     * 当前播放状态，参见 PlayerStatus
     */
    fun onPlayerStatus(playStatus: Int)

    /**
     * 当前播放进度
     */
    fun onProgress(currentDuration: Int, totalDuration: Int)

    /**
     * 播放模式，随机，顺序，单曲
     */
    fun onPlayMode(playMode: Int)

    /**
     * 重置
     */
    fun onReset()
}