package com.jacky.wanandroidkotlin.test

import android.content.Context
import android.content.Intent
import android.graphics.drawable.LevelListDrawable
import android.graphics.drawable.TransitionDrawable
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.util.LruCache
import android.util.SparseArray
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.jetpack.binding.TwoActivity
import com.jacky.wanandroidkotlin.jetpack.navigation.WelcomeActivity
import com.jacky.wanandroidkotlin.test.TestActivity.Constants.NUM_B
import com.jacky.wanandroidkotlin.ui.baidumap.BaiDuMapLearnActivity
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.demos.NetEasyDemoActivity
import com.jacky.wanandroidkotlin.util.CountDownClock
import com.jacky.wanandroidkotlin.util.CountDownClock.Companion.createCountDownClock
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.viewClickListener
import com.zenchn.support.router.Router
import com.zenchn.support.utils.LoggerKit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*

/**
 *
顶层声明常量
 */
private const val NUM_A: String = "顶层声明常量"

class TestActivity : BaseActivity(), CoroutineScope by MainScope() {

    /**
     * 后期初始化属性
     */
    private lateinit var mTvInfo: TextView

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

        mTvInfo = findViewById(R.id.tv_info)

        findViewById<Button>(R.id.btn_wifi).setOnClickListener { _ ->
            (applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager)?.apply {
                val wifiInfo = connectionInfo
                mTvInfo.text = wifiInfo.toString()
            }

        }

        viewClickListener(R.id.bt) {
            countDownClock = createCountDownClock(10, lifecycle) { time ->
                (it as? TextView)?.text = "倒计时：$time"
            }.apply { start() }
        }

        viewClickListener(R.id.bt_oom) {
//            testCreateThread()
            testLruCache()
        }

        viewClickListener(R.id.btn_baidu_map) {
            BaiDuMapLearnActivity.launch(this, 1, true, null)
        }

        viewClickListener(R.id.bt_main) {
            WelcomeActivity.launch(this)
        }

        viewClickListener(R.id.btn_netease) {
            startActivity(Intent(this, NetEasyDemoActivity::class.java))
        }

        viewClickListener(R.id.bt_test_data_binding) {
            startActivity(Intent(this, TwoActivity::class.java))
        }

        viewClickListener(R.id.bt_test_web) {
            getView<EditText>(R.id.et_url).text?.toString()?.trim()?.let { testUrl ->
                BrowserActivity.launch(this, testUrl)
            }
        }

        //对象实例化
        /**
         * 我是块注释,块注释允许嵌套
         * /**
         * 我也是块注释
         * */
         */
        val man = Man(25, "张三")
        val eat = man.eat("面包", lazyString)
        man.toast(this, eat)
        getView<Button>(R.id.bt).text = String.format(Locale.CHINA, "$eat:$NUM_A=$NUM_B-$NUM_C")

        val zhang = Person(5)
        println("zhang:$zhang")
        val wang = Person(52, "老王")
        println("zhang:$wang")

        //drawable test
        val levelListDrawable =
            ContextCompat.getDrawable(this, R.drawable.level_list_drawable) as? LevelListDrawable
        levelListDrawable?.level = 3
        getView<View>(R.id.v_level_list).background = levelListDrawable

        val transitionDrawable =
            ContextCompat.getDrawable(this, R.drawable.transition_drawable) as? TransitionDrawable
        getView<View>(R.id.v_transition).background = transitionDrawable
        transitionDrawable?.startTransition(3000)
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
            Log.e("oom" , "i..." + i++)
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
                        Log.e("oom","i..." + getProcessData())
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

    override fun onStart() {
        super.onStart()
        println("onStart")
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
    }

    override fun onRestart() {
        super.onRestart()
        println("onRestart")
    }

    override fun onPause() {
        super.onPause()
        println("onPause")
    }

    override fun onStop() {
        super.onStop()
        println("onStop")
        countDownClock?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy")
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
