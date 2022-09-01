package com.jacky.wanandroidkotlin.ui.demos

/**
 * @author:Hzj
 * @date  :2022/5/13
 * desc  ：MotionLayout test
 * record：
 */
import android.app.Activity
import android.app.Application
import android.widget.ImageView
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.databinding.ActivityDemoMotionSceneOneBinding
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.support.router.Router


class MotionLayoutDemoActivity :
    BaseVMActivity<ActivityDemoMotionSceneOneBinding, MotionLayoutDemoViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_demo_motion_scene_one

    override fun initWidget() {
        getView<ImageView>(R.id.iv_2).apply {

        }
    }

    override val startObserve: MotionLayoutDemoViewModel.() -> Unit = {

    }

    companion object {
        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(MotionLayoutDemoActivity::class.java)
                .launch()
        }
    }
}

class MotionLayoutDemoViewModel(application: Application) : BaseViewModel(application) {

}