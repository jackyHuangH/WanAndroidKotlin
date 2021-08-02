package com.jacky.wanandroidkotlin.wrapper.musicplay

/**
 * @author:Hzj
 * @date  :2020/12/14
 * desc  ：播放过程回调接口
 * record：
 */
interface IPlayerStatusListener {
    /**
     * 缓冲更新
     */
    fun onBufferingUpdate(percent:Int)

    /**
     * 播放完成
     */
    fun onPlayComplete()
}