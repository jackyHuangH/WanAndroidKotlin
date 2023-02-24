package com.jacky.wanandroidkotlin.wrapper.musicplay

import com.hjq.toast.Toaster
import com.jacky.wanandroidkotlin.model.local.ContextModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * @author:Hzj
 * @date  :2020/12/14
 * desc  ：管理音乐播放列表
 * record：
 */
class PlayListManager {

    /**
     * 单例创建PlayerManager
     */
    companion object {
        val instance: PlayListManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PlayListManager()
        }
    }

    //当前播放列表
    private var currentPlayList = mutableListOf<AudioBean>()

    //当前的音乐歌曲信息
    private var currentAudioBean: AudioBean? = null

    /**
     * 当前播放音频对象在对应播放列表的位置
     */
    private var currentIndex = 0

    //本地播放列表
    private var localPlayList = mutableListOf<AudioBean>()

    /**
     * 播放模式，默认为顺序播放
     */
    private var playMode = PlayModeEnum.ORDER_PLAY.playMode

    init {
        //初始化时通过io线程读取本地音乐文件
        GlobalScope.launch(Dispatchers.IO) {
            localPlayList = ContextModel.getApplicationContext().readLocalMusicList()
            currentPlayList.clear()
            currentPlayList.addAll(localPlayList)
        }
    }

    /**
     *获取当前播放曲目列表
     */
    fun getCurrentAudioList(): MutableList<AudioBean> = currentPlayList

    /**
     * 第一次播放的音频，默认列表第一个
     */
    fun startPlayAudio(): AudioBean? {
        if (currentPlayList.isNotEmpty()) {
            currentAudioBean = currentPlayList[0]
        }
        return currentAudioBean
    }

    /**
     * 下一首
     */
    fun nextAudio(): AudioBean? {
        if (currentPlayList.isNotEmpty()) {
            val totalSize = currentPlayList.size
            when (playMode) {
                PlayModeEnum.ORDER_PLAY.playMode -> {
                    //顺序播放
                    currentIndex = if (currentIndex < totalSize - 1) {
                        currentIndex + 1
                    } else 0
                }
                PlayModeEnum.RANDOM_PLAY.playMode -> {
                    //随机播放
                    currentIndex = Random.nextInt(0, totalSize - 1)
                }
                PlayModeEnum.SINGLE_CYCLE.playMode -> {
                    //单曲循环，不做处理
                }
            }
            currentAudioBean = currentPlayList[currentIndex]
        } else {
            //当前播放列表为空，返回null
            currentAudioBean = null
        }
        return currentAudioBean
    }

    /**
     * 上一首
     */
    fun previousAudio(): AudioBean? {
        if (currentPlayList.isNotEmpty()) {
            val totalSize = currentPlayList.size
            when (playMode) {
                PlayModeEnum.ORDER_PLAY.playMode -> {
                    //顺序播放
                    currentIndex = if (currentIndex > 0) {
                        currentIndex - 1
                    } else totalSize - 1
                }
                PlayModeEnum.RANDOM_PLAY.playMode -> {
                    //随机播放
                    currentIndex = Random.nextInt(0, totalSize - 1)
                }
                PlayModeEnum.SINGLE_CYCLE.playMode -> {
                    //单曲循环，不做处理
                }
            }
            currentAudioBean = currentPlayList[currentIndex]
        } else {
            //当前播放列表为空，返回null
            currentAudioBean = null
        }
        return currentAudioBean
    }

    /**
     * 切换播放模式
     */
    fun switchPlayMode(): Int {
        val modeDesc: String
        playMode = when (playMode) {
            PlayModeEnum.ORDER_PLAY.playMode -> {
                modeDesc = PlayModeEnum.RANDOM_PLAY.modeDesc
                PlayModeEnum.RANDOM_PLAY.playMode
            }
            PlayModeEnum.RANDOM_PLAY.playMode -> {
                modeDesc = PlayModeEnum.SINGLE_CYCLE.modeDesc
                PlayModeEnum.SINGLE_CYCLE.playMode
            }
            PlayModeEnum.SINGLE_CYCLE.playMode -> {
                modeDesc = PlayModeEnum.ORDER_PLAY.modeDesc
                PlayModeEnum.ORDER_PLAY.playMode
            }
            else -> {
                modeDesc = PlayModeEnum.ORDER_PLAY.modeDesc
                PlayModeEnum.ORDER_PLAY.playMode
            }
        }
        Toaster.show(modeDesc)
        return playMode
    }

    fun getCurrentAudioBean(): AudioBean? = currentAudioBean

    //设置当前正在播放的对象
    fun setCurrentAudioBean(audioBean: AudioBean) {
        currentAudioBean = audioBean
        currentIndex = getIndexByAudio(audioBean)
    }

    /**
     * 通过currentAudio获取所在的index
     * 之所以没有全局开放一个index,是为了尽可能的降低 index 的操作权限
     */
    private fun getIndexByAudio(audioBean: AudioBean): Int {
        val size = currentPlayList.size
        for (index in 0 until size) {
            if (audioBean == currentPlayList[index]) {
                return index
            }
        }
        //默认返回0
        return 0
    }

    fun getPlayMode() = playMode

    /**
     * 清除播放器播放资源
     */
    fun clear() {
        currentAudioBean = null
    }
}

object PlayListType {
    //本地播放列表
    const val LOCAL_PLAY_LIST = 1

    //收藏播放列表
    const val COLLECT_PLAY_LIST = 2

    //历史播放列表
    const val HISTORY_PLAY_LIST = 3
}

object PlayMode {
    //顺序播放
    const val PLAY_IN_ORDER = 4

    //随机播放
    const val PLAY_IN_RANDOM = 5

    //单曲循环
    const val SINGLE_CYCLE = 6
}