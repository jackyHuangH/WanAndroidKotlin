package com.jacky.wanandroidkotlin.test

/**
 * @author:Hzj
 * @date  :2022/11/17
 * desc  ：自定义view测试页面
 * record：
 */
import android.app.Activity
import android.app.Application
import com.jacky.support.router.Router
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.databinding.ActivityCustomViewBinding
import com.jacky.wanandroidkotlin.util.setOnAntiShakeClickListener


class CustomViewActivity :
    BaseVMActivity<ActivityCustomViewBinding, CustomViewActivityViewModel>() {
    private var mPoint1 = true

    override fun getLayoutId(): Int = R.layout.activity_custom_view

    override fun initWidget() {
        mViewBinding.btControlPoint.text = if (mPoint1) "控制点1" else "控制点2"
        mViewBinding.btControlPoint.setOnAntiShakeClickListener {
            mPoint1 = !mPoint1
            mViewBinding.bezierView.setCurrentControlPoint(if (mPoint1) 1 else 2)
            mViewBinding.btControlPoint.text = if (mPoint1) "控制点1" else "控制点2"
        }
    }

    override val startObserve: CustomViewActivityViewModel.() -> Unit = {

    }

    /**
     * 伴生类:静态单例内部类
     */
    companion object Num {
        const val TAG = "CUSTOME_VIEW"

        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(CustomViewActivity::class.java)
                .launch()
        }
    }
}

class CustomViewActivityViewModel(application: Application) : BaseViewModel(application) {

}