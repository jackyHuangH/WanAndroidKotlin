package com.jacky.wanandroidkotlin.test

import android.content.Context
import android.content.Intent
import android.graphics.drawable.LevelListDrawable
import android.graphics.drawable.TransitionDrawable
import android.net.wifi.WifiManager
import android.os.Process
import android.util.Log
import android.util.LruCache
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.hjq.toast.Toaster
import com.jacky.support.router.Router
import com.jacky.support.utils.LoggerKit
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.aidltest.AidlTestActivity
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.databinding.ActivityTestBinding
import com.jacky.wanandroidkotlin.jetpack.binding.TwoActivity
import com.jacky.wanandroidkotlin.jetpack.navigation.WelcomeActivity
import com.jacky.wanandroidkotlin.jetpack.nike.NikeMainActivity
import com.jacky.wanandroidkotlin.model.entity.TodayInHistoryEntity
import com.jacky.wanandroidkotlin.ui.baidumap.BaiDuMapLearnActivity
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.demos.MotionLayoutDemoActivity
import com.jacky.wanandroidkotlin.util.CountDownClock
import com.jacky.wanandroidkotlin.util.CountDownClock.Companion.createCountDownClock
import com.jacky.wanandroidkotlin.util.DisplayUtils
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.viewClickListener
import kotlinx.coroutines.*
import java.io.IOException
import java.io.RandomAccessFile

/**
 *
顶层声明常量
 */
private const val NUM_A: String = "顶层声明常量"

class TestActivity : BaseVMActivity<ActivityTestBinding, TestViewModel>(),
    CoroutineScope by MainScope() {

    private var countDownClock: CountDownClock? = null

    /**
     * 声明一个延迟初始化（懒加载）属性
     */
    private val lazyString: String by lazy { "我是懒加载的内容" }

    /**
     * object对象声明，它相当于Java中一种形式的单例类
     */
    object Constants {
        const val NUM_B = "object修饰的类中声明常量"
    }

    override fun getLayoutId(): Int = R.layout.activity_test

    override fun initWidget() {
        findViewById<Button>(R.id.btn_wifi).setOnClickListener { _ ->
            (applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager)?.apply {
                val wifiInfo = connectionInfo
                showMessage(wifiInfo.toString())
            }

        }

        //测试状态栏透明
        viewClickListener(R.id.bt_test_status_bar) {
            startActivity(Intent(this, StatusBarTestActivity::class.java))
        }

        viewClickListener(R.id.bt_countdown) {
            startCountDown()
        }

        viewClickListener(R.id.bt_oom) {
//            testCreateThread()
            testLruCache()
        }

        viewClickListener(R.id.bt_aidl) {
            startActivity(Intent(this, AidlTestActivity::class.java))
        }

        viewClickListener(R.id.btn_baidu_map) {
            BaiDuMapLearnActivity.launch(this, 1, true, null)
        }

        viewClickListener(R.id.bt_main) {
            WelcomeActivity.launch(this)
        }

        viewClickListener(R.id.btn_motion_demo) {
            MotionLayoutDemoActivity.launch(this)
        }
        //获取历史上的今天，方式1
//        mViewModel.getTodayInHistory()
        //方式2
        mViewModel.getTodayInHistory2().observe(this) { data ->
            if (data != null && data.error_code == 0) {
                startTsLooper(data.result.orEmpty())
            }
        }

        viewClickListener(R.id.btn_coolapk) {
            startActivity(Intent(this, NestedRecyclerViewTestActivity::class.java))
        }

        viewClickListener(R.id.btn_my_nested) {
            startActivity(Intent(this, MyNestedScrollTestActivity::class.java))
        }

        viewClickListener(R.id.bt_test_data_binding) {
            startActivity(Intent(this, TwoActivity::class.java))
        }

        viewClickListener(R.id.bt_room) {
            startActivity(Intent(this, RoomTestActivity::class.java))
        }

        viewClickListener(R.id.btn_phone) {
            startActivity(Intent(this, NikeMainActivity::class.java))
        }

        viewClickListener(R.id.bt_test_web) {
            getView<EditText>(R.id.et_url).text?.toString()?.trim()?.let { testUrl ->
                BrowserActivity.launch(this, testUrl)
            }
        }

        //drawable test
        val levelListDrawable =
            ContextCompat.getDrawable(this, R.drawable.level_list_drawable) as? LevelListDrawable
        levelListDrawable?.level = 3
        getView<View>(R.id.v_level_list).background = levelListDrawable

        val transitionDrawable =
            ContextCompat.getDrawable(this, R.drawable.transition_drawable) as? TransitionDrawable
        getView<View>(R.id.v_transition).background = transitionDrawable
        transitionDrawable?.startTransition(3000)

        initScrollView()
        initSeekBar()
    }

    private fun initSeekBar() {
        mViewBinding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mViewBinding.pb.progress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    /**
     * 监听scrollView滑动,设置指示器
     */
    private fun initScrollView() {
        //获取屏幕高度
        val screenHeight = DisplayUtils.screenHeight()
        //获取scrollview高度，主动调用测量方法
        mViewBinding.scrollView.measure(0, 0)
        val scrollViewHeight = mViewBinding.scrollView.getChildAt(0).measuredHeight
        Log.d(TAG, "scrollview H:$scrollViewHeight ")
        //计算可以滑动的距离差值
        val deltaY = scrollViewHeight - screenHeight
        Log.d(TAG, "deltaY:$deltaY")
        //动态计算指示器背景高度,要加上滑块自身高度
        mViewBinding.flIndicatorBg.layoutParams.apply {
            height = deltaY + DisplayUtils.dp2px(30)
        }

        //固定高度指示器可滑动距离=总高度-滑块高度
        val deltaY2 = DisplayUtils.dp2px(80 - 30)
        mViewBinding.scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            //上下平移指示器
            mViewBinding.vIndicator.translationY = scrollY.toFloat()
            //固定高度指示器，按比例计算滑动的距离
            val scaleY = (scrollY.toFloat() / deltaY) * deltaY2
            Log.d(TAG, "scrollY:$scrollY-----scaleY:$scaleY")
            mViewBinding.vIndicator2.translationY = scaleY
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //获取屏幕高度
        val screenHeight = DisplayUtils.screenHeight()
        //获取scrollview高度,有延迟,不推荐
//        val scrollViewHeight = mViewBinding.scrollView.getChildAt(0).measuredHeight
//        Log.d(TAG, "onWindowFocusChanged H:$scrollViewHeight ")
        //动态计算指示器背景高度
//        mViewBinding.flIndicatorBg.layoutParams.apply {
//            height = scrollViewHeight - screenHeight + DisplayUtils.dp2px(30)
//        }
    }

    private fun startCountDown() {
        //倒计时
        countDownClock = createCountDownClock(10, lifecycle) { time ->
            Toaster.show("倒计时：$time")
        }.apply { start() }
    }

    private fun testLruCache() {
        val lruCache = object : LruCache<Int, Int>(88) {
            override fun sizeOf(key: Int?, value: Int?): Int {
                return 1
            }
        }
        for (i in 0..10) {
            lruCache.put(i, i)
        }
        LoggerKit.d("lruCache:${lruCache.toString()}")
    }

    //一直创建线程
    private fun testCreateThread() {
        var i = 0
        while (true) {
            Log.e("oom", "i..." + i++)
            try {
                if (i == 1801) {
                    Thread.sleep(200);
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            Thread({
                try {
                    if (i == 1800) {
                        //获取proc/pid/status状态
                        Log.e("oom", "i..." + getProcessData())
                    }
                    //保证线程尽量活着
                    Thread.sleep(100000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }, "i..." + i).start()
        }

    }

    private fun getProcessData(): String {
        try {
            val pid = Process.myPid().toString()
            val reader2 = RandomAccessFile("/proc/$pid/status", "r")
            val sb = StringBuilder()
            var tempStr: String?
            while (reader2.readLine().also { tempStr = it } != null) {
                Log.d("ddebug", tempStr.orEmpty())
                sb.append(tempStr).append("\n")
            }
            reader2.close()
            return sb.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun startTsLooper(list: List<TodayInHistoryEntity>) {
        if (list.isEmpty()){
            return
        }
        var index = 0
        //TextSwitcher
        mViewBinding.ts.apply {
            setFactory {
                TextView(this@TestActivity).apply {
                    gravity = Gravity.CENTER
                    textSize = 17F
                }
            }
            setOnClickListener {
                val item=list[index++%list.size]
                this.setText("${item.date}:${item.title}")
            }
        }
        //轮播实现
        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                launch(Dispatchers.Main) {
                    val item=list[index++%list.size]
                    mViewBinding.ts.setText("${item.date}:${item.title}")
                }
                delay(3000)

                Log.d(TAG, "loop")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        println("onStop")
        countDownClock?.stop()
    }


    override val startObserve: TestViewModel.() -> Unit = {
        mTodayInHistoryData.observe(this@TestActivity) { list ->
            startTsLooper(list)
        }
    }

    /**
     * 伴生类:静态单例内部类
     */
    companion object Num {
        const val NUM_C = "伴生对象中声明"
        const val TAG = "TEST"

        fun launch(from: FragmentActivity) {
            Router
                .newInstance()
                .from(from)
                .to(TestActivity::class.java)
                .launch()
        }
    }
}