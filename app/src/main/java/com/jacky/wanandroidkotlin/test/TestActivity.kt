package com.jacky.wanandroidkotlin.test

import android.graphics.drawable.LevelListDrawable
import android.graphics.drawable.TransitionDrawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.test.TestActivity.Constants.NUM_B
import com.zenchn.support.router.Router
import com.zenchn.support.widget.tips.SuperToast
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*
import kotlin.collections.ArrayList

/**
 *
顶层声明常量
 */
private const val NUM_A: String = "顶层声明常量"

class TestActivity : BaseActivity() {

    /**
     * 后期初始化属性
     */
    private lateinit var mRlv: RecyclerView

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

    override fun getLayoutRes(): Int = R.layout.activity_test

    override fun initWidget() {
        mRlv = findViewById(R.id.list)
        mRlv.layoutManager = LinearLayoutManager(this)
        val datas = ArrayList<String>()
        for (i in 0 until 25) {
            datas.add("元素$i")
        }

        sw_btn.setOnCheckedChangeListener { _, isChecked ->
            bt.text = if (isChecked) "打开" else "关闭"
        }

        bt.setOnClickListener {

        }

        bt_main.setOnClickListener {
            SuperToast.showDefaultMessage(this, "跳转主页")
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
        bt.text = String.format(Locale.CHINA, "$eat:$NUM_A=$NUM_B-$NUM_C")

        val zhang = Person(5)
        println("zhang:$zhang")
        val wang = Person(52, "老王")
        println("zhang:$wang")

        //drawable test
        val levelListDrawable =
            ContextCompat.getDrawable(this, R.drawable.level_list_drawable) as? LevelListDrawable
        levelListDrawable?.level = 3
        v_level_list.background = levelListDrawable

        val transitionDrawable =
            ContextCompat.getDrawable(this, R.drawable.transition_drawable) as? TransitionDrawable
        v_transition.background = transitionDrawable
        transitionDrawable?.startTransition(3000)
    }

    /**
     * 伴生类:静态单例内部类
     */
    companion object Num {
        const val NUM_C = "伴生对象中声明"

        fun launch(from: FragmentActivity) {
            Router
                .newInstance()
                .from(from)
                .to(TestActivity::class.java)
                .launch()
        }
    }
}
