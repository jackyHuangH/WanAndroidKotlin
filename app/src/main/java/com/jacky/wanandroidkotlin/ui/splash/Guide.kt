package com.jacky.wanandroidkotlin.ui.splash


import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.gyf.immersionbar.ImmersionBar
import com.jacky.support.router.Router
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseFragment
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.databinding.ActivityGuideBinding
import com.jacky.wanandroidkotlin.databinding.FragmentGuideBinding
import com.jacky.wanandroidkotlin.ui.adapter.BaseViewPager2Adapter
import com.jacky.wanandroidkotlin.util.setOnAntiShakeClickListener

/**
 * @author:Hzj
 * @date  :2022/10/8
 * desc  ：引导页,使用lottie动画
 * record：
 */
class GuideActivity : BaseVMActivity<ActivityGuideBinding, GuideViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_guide

    override fun initStatusBar() {
        ImmersionBar.with(this).apply {
            transparentStatusBar()
            statusBarDarkFont(true)
            fitsSystemWindows(true)
            init()
        }
    }

    override fun initWidget() {
        mViewBinding.tvNext.setOnAntiShakeClickListener {
            val currentItem = mViewBinding.vpGuide.currentItem
            if (currentItem < GUIDE_COUNT - 1) {
                mViewBinding.vpGuide.currentItem = currentItem + 1
            } else {
                //跳转到startActivity
                StartActivity.launch(this)
            }
        }
        mViewBinding.tvSkip.setOnAntiShakeClickListener {
            //跳转到startActivity
            StartActivity.launch(this)
        }
        val fragmentList = mutableListOf<GuideFragment>().apply {
            for (i in 0 until GUIDE_COUNT) {
                add(GuideFragment.getInstance(i))
            }
        }
        val vpAdapter = BaseViewPager2Adapter(supportFragmentManager, lifecycle, fragmentList)
        mViewBinding.vpGuide.adapter = vpAdapter
        //TabLayout绑定viewpager2
        TabLayoutMediator(
            mViewBinding.tabIndicator,
            mViewBinding.vpGuide
        ) { tab, index -> }.attach()

    }

    override val startObserve: GuideViewModel.() -> Unit = {

    }


    companion object {
        const val GUIDE_COUNT = 6

        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(GuideActivity::class.java)
                .launch()
        }
    }
}

class GuideViewModel(application: Application) : BaseViewModel(application) {

}


/**
 * 引导页内容，使用Lottie动画
 */
class GuideFragment : BaseFragment<FragmentGuideBinding>() {
    private val mLottieResList by lazy {
        mutableListOf(
            R.raw.lottie_delivery_boy_bumpy_ride,
            R.raw.lottie_developer,
            R.raw.lottie_girl_with_a_notebook,
            R.raw.lottie_messaging,
            R.raw.lottie_splash_animation,
            R.raw.lottie_watch_videos
        )
    }

    override fun isStatusBarEnabled(): Boolean {
        return false
    }

    override fun getLayoutId(): Int = R.layout.fragment_guide

    companion object {
        const val PARAM_INDEX = "index"

        fun getInstance(index: Int): GuideFragment {
            val fragment = GuideFragment()
            val bundle = Bundle()
            bundle.putInt(PARAM_INDEX, index)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initWidget() {
        super.initWidget()
        val index = requireArguments().getInt(PARAM_INDEX)
        mViewBinding.lottieView.setAnimation(mLottieResList[index])
    }

}