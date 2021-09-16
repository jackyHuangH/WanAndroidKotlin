package com.jacky.wanandroidkotlin.test

import android.content.Context
import android.content.Intent
import android.graphics.drawable.LevelListDrawable
import android.graphics.drawable.TransitionDrawable
import android.net.wifi.WifiManager
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
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.viewClickListener
import com.zenchn.support.router.Router
import java.util.*

/**
 *
顶层声明常量
 */
private const val NUM_A: String = "顶层声明常量"

class TestActivity : BaseActivity() {

    /**
     * 后期初始化属性
     */
    private lateinit var mTvInfo: TextView

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

        viewClickListener(R.id.btn_baidu_map){
            BaiDuMapLearnActivity.launch(this,1,true,null)
        }

        viewClickListener(R.id.bt_main) {
            WelcomeActivity.launch(this)
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
