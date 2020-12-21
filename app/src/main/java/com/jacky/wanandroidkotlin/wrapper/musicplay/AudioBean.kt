package com.jacky.wanandroidkotlin.wrapper.musicplay

/**
 * @author:Hzj
 * @date  :2020/12/14
 * desc  ：mp3文件封装类,遵循mp3格式
 * record：
 */
data class AudioBean(
    /**
     * 歌曲id
     */
    var id: Long = 0,

    /**
     * 歌曲名
     */
    var name: String? = null,

    /**
     * 歌手
     */
    var singer: String? = null,

    /**
     * 歌曲所占空间大小
     */
    var size: Long = 0,

    /**
     * 歌曲时间长度
     */
    var duration: Int = 0,

    /**
     * 歌曲地址
     */
    var path: String? = null,

    /**
     * 专辑封面id
     */
    var albumId: Long = 0,

    /**
     * 排序id
     */
    var sortId: Long = 0,

    /**
     * 所属播放列表
     */
    var playListType: Int = PlayListType.LOCAL_PLAY_LIST,

    /**
     * 是否选中播放
     */
    var hasSelected: Boolean = false
)