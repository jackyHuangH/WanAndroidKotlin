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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gyf.immersionbar.ImmersionBar
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.databinding.ActivityMusicPlayBinding
import com.jacky.wanandroidkotlin.jetpack.binding.AnimBinding
import com.jacky.wanandroidkotlin.ui.adapter.MusicListAdapter
import com.jacky.wanandroidkotlin.util.StatusBarUtil
import com.jacky.wanandroidkotlin.util.formatMusicTime
import com.jacky.wanandroidkotlin.util.setOnAntiShakeClickListener
import com.jacky.wanandroidkotlin.wrapper.childViewExt
import com.jacky.wanandroidkotlin.wrapper.musicplay.*
import com.jacky.support.router.Router
import com.jacky.support.utils.AndroidKit


class MusicPlayActivity : AppCompatActivity(), AudioObserver {

    private val mViewModel by viewModels<MusicPlayViewModel>()
    private lateinit var mActivityBinding: ActivityMusicPlayBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val musicListAdapter by lazy { MusicListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        //1.设置dataBinding绑定关系
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_music_play)
        super.onCreate(savedInstanceState)
        //2.绑定数据源!!!!!不绑定数据源无法实现数据驱动UI，切记！！！！
        mActivityBinding.vm = mViewModel
        //3.绑定lifecycle
        mActivityBinding.lifecycleOwner = this
        initWidget()
        initStatusBar()
    }

    private fun initStatusBar() {
        ImmersionBar.with(this).apply {
            transparentStatusBar()
            statusBarDarkFont(false)
            init()
        }
        StatusBarUtil.setStatusBarMargin(this, mActivityBinding.ibtBack)
    }

    private fun initWidget() {
        MusicPlayManager.register(this)
        mActivityBinding.ibtBack.setOnAntiShakeClickListener { onBackPressed() }
        initSeekBar()
        initBottomSheet()
        initClick()
    }

    private fun initBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val bottomView =
            LayoutInflater.from(this)
                .inflate(R.layout.layout_music_list_bottom_dialog, null)
                .apply {
                    post {
                        val lp = this.layoutParams
                        lp.height = AndroidKit.Dimens.getScreenHeight() * 2 / 3
                        this.layoutParams = lp
                    }
                    val tvCount = findViewById<TextView>(R.id.tv_audio_count)
                    childViewExt<RecyclerView>(R.id.rv_audio) {
                        initRecyclerView(tvCount, this)
                    }
                }
        bottomSheetDialog.setContentView(bottomView)
        bottomSheetDialog.behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.d("MusicBottom", "newState:$newState")
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        })
    }

    private fun initRecyclerView(textView: TextView, rv: RecyclerView) {
        rv.apply {
            layoutManager = LinearLayoutManager(this@MusicPlayActivity)
            setHasFixedSize(true)
            adapter = musicListAdapter.apply {
                setHasStableIds(true)
                setOnItemClickListener { adapter, view, position ->
                    val item = adapter.data[position] as? AudioBean
                    item?.let {
                        MusicPlayManager.playNewAudio(it)
                        musicListAdapter.updateSelect(it)
                    }
                }
            }
        }
        val audioList = MusicPlayManager.getCurrentAudioList()
        musicListAdapter.setList(audioList)
        textView.text = "歌曲列表（${audioList.size}）"
    }

    //点击事件监听
    private fun initClick() {

        mActivityBinding.ibtCollect.apply {
            setOnAntiShakeClickListener {
                //收藏
                isSelected = isSelected.not()
            }
        }

        mActivityBinding.ivMode.setOnAntiShakeClickListener {
            //播放模式
            MusicPlayManager.switchPlayMode()
        }
        mActivityBinding.ivPrevious.setOnAntiShakeClickListener {
            //上一首
            MusicPlayManager.previousAudio()
        }
        mActivityBinding.ivPlay.setOnAntiShakeClickListener {
            //播放、暂停
            MusicPlayManager.playOrPause()
        }
        mActivityBinding.ivNext.setOnAntiShakeClickListener {
            //下一首
            MusicPlayManager.nextAudio()
        }
        mActivityBinding.ivMusicList.setOnAntiShakeClickListener {
            //播放列表
            bottomSheetDialog.show()
        }
    }

    private fun initSeekBar() {
        mActivityBinding.seekBar.apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    mViewModel.playDurationString.set(formatMusicTime(seekBar.progress))
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    //拖动播放
                    MusicPlayManager.seekToPlay(seekBar.progress)
                }

            })
        }
    }

    override fun onAudioBean(audioBean: AudioBean) {
        mViewModel.audioInfo.set(audioBean)
        mViewModel.maxProgress.set(audioBean.duration)
        mViewModel.totalDurationString.set(formatMusicTime(audioBean.duration))
        musicListAdapter.updateSelect(audioBean)
    }

    override fun onPlayerStatus(playStatus: Int) {
        val isPlaying =
            (playStatus == PlayerStatus.PLAY_RESUME || playStatus == PlayerStatus.PLAY_START)
        mViewModel.playStatusSelected.set(isPlaying)
        Log.d("MusicAct", "oldStatus:${mViewModel.playStatus.get()}----newStatus:$playStatus")
        mViewModel.playStatus.set(playStatus)
    }

    override fun onProgress(currentDuration: Int, totalDuration: Int) {
        mViewModel.playProgress.set(currentDuration)
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

    override fun onDestroy() {
        MusicPlayManager.unregister(this)
        AnimBinding.releaseAnim()
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

open class MusicPlayViewModel(application: Application) : BaseViewModel(application) {
    //播放模式按钮图片
    val playModePic: ObservableField<Int> = ObservableField<Int>(R.drawable.play_order)

    //播放按钮状态，是否在播放
    val playStatusSelected: ObservableField<Boolean> = ObservableField()

    //播放按钮状态，是否在播放
    // 如果整型字面量的值在-128到127之间，那么不会new新的Integer对象，而是直接引用常量池中的Integer对象，
    // 所以playStatus设置大于127就会自动装箱成Integer，对应的引用也就不同，达到更新目的。
    val playStatus = ObservableField<Int>()

    //总进度
    val maxProgress: ObservableField<Int> = ObservableField()

    //播放进度
    val playProgress: ObservableField<Int> = ObservableField()

    //总时长
    val totalDurationString: ObservableField<String> = ObservableField()

    //播放时长
    val playDurationString: ObservableField<String> = ObservableField()

    //音乐信息
    val audioInfo: ObservableField<AudioBean> = ObservableField()

    fun reset() {
        playStatusSelected.set(false)
        maxProgress.set(0)
        playProgress.set(0)
        playStatus.set(PlayerStatus.PLAY_PAUSE)
        playDurationString.set("00:00")
        totalDurationString.set("00:00")
        audioInfo.set(AudioBean(name = "暂无曲目", singer = "", albumId = -1L))
    }
}