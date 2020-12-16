package com.jacky.wanandroidkotlin.ui.music

/**
 * @author:Hzj
 * @date  :2020/12/11
 * desc  ：仿网易云音乐播放,使用dataBinding学习MVVM
 * record：
 */
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.databinding.ActivityMusicPlayBinding
import com.jacky.wanandroidkotlin.util.formatMusicTime
import com.jacky.wanandroidkotlin.util.setOnAntiShakeClickListener
import com.jacky.wanandroidkotlin.wrapper.musicplay.*
import com.zenchn.support.router.Router
import kotlinx.android.synthetic.main.activity_music_play.*


class MusicPlayActivity : BaseVMActivity<MusicPlayViewModel>(), AudioObserver {

    private lateinit var mActivityBinding: ActivityMusicPlayBinding

    override fun getLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        //1.设置dataBinding绑定关系
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_music_play)
        super.onCreate(savedInstanceState)
        //2.绑定数据源!!!!!不绑定数据源无法实现数据驱动UI，切记！！！！
        mActivityBinding.vm = mViewModel
        //3.绑定lifecycle
        mActivityBinding.lifecycleOwner = this
    }

    override fun initWidget() {
        MusicPlayManager.register(this)
        ibt_back.setOnAntiShakeClickListener { onBackPressed() }
        initSeekBar()
        initClick()
    }

    //TODO 点击事件监听
    private fun initClick() {
        ibt_collect.setOnAntiShakeClickListener {
            //收藏
        }
        ivMode.setOnAntiShakeClickListener {
            //播放模式
            MusicPlayManager.switchPlayMode()
        }
        ivPrevious.setOnAntiShakeClickListener {
            //上一首
        }
        ivPlay.setOnAntiShakeClickListener {
            //播放、暂停
            MusicPlayManager.playOrPause()
        }
        ivNext.setOnAntiShakeClickListener {
            //下一首
        }
        ivMusicList.setOnAntiShakeClickListener {
            //播放列表
        }
    }

    private fun initSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tv_start_time.text = formatMusicTime(seekBar.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //拖动播放
            }

        })
    }

    override fun onAudioBean(audioBean: AudioBean) {
    }

    override fun onPlayerStatus(playStatus: Int) {
        val isPlaying =
            (playStatus == PlayerStatus.PLAY_RESUME || playStatus == PlayerStatus.PLAY_START)
        mViewModel.playStatusSelected.set(isPlaying)
    }

    override fun onProgress(currentDuration: Int, totalDuration: Int) {
    }

    override fun onPlayMode(playMode: Int) {
        val modeResId: Int = when (playMode) {
            PlayModeEnum.ORDER_PLAY.playMode -> R.drawable.play_order
            PlayModeEnum.RANDOM_PLAY.playMode -> R.drawable.play_random
            PlayModeEnum.SINGLE_CYCLE.playMode -> R.drawable.play_single
            else -> R.drawable.play_order
        }
        mViewModel.playModePic.set(modeResId)
    }

    override fun onReset() {
        mViewModel.reset()
    }

    override val startObserve: MusicPlayViewModel.() -> Unit = {

    }

    override fun onDestroy() {
        MusicPlayManager.unregister(this)
        super.onDestroy()
    }

    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(MusicPlayActivity::class.java)
                .launch()
        }
    }
}

class MusicPlayViewModel(application: Application) : BaseViewModel(application) {
    //播放模式按钮图片
    val playModePic: ObservableField<Int> = ObservableField<Int>(R.drawable.play_order)

    //播放按钮状态，是否在播放
    val playStatusSelected: ObservableField<Boolean> = ObservableField<Boolean>()

    fun reset(){
        playStatusSelected.set(false)
    }
}