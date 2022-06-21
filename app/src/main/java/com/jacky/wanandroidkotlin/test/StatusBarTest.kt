package com.jacky.wanandroidkotlin.test

/**
 * @author:Hzj
 * @date  :2022/6/21
 * desc  ：主题要设置状态栏透明，使用Palette实现沉浸式状态栏
 * record：
 */

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.palette.graphics.Palette
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.databinding.ActivityStatusTestBinding


class StatusBarTestActivity : BaseVMActivity<ActivityStatusTestBinding, StatusBarTestViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_status_test

    override fun initStatusBar() {
        //代码设置状态栏颜色，或者通过设置theme
        setStatusBarColor(this,ContextCompat.getColor(this,R.color.transparent))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //代码设置状态栏颜色，或者通过设置theme
//        setStatusBarColor(this,ContextCompat.getColor(this,R.color.transparent))
    }

    override fun initWidget() {
        mViewBinding.darkImageBtn.setOnClickListener {
            setBgImageByResource(R.drawable.bg_splash)
        }
        mViewBinding.lightImageBtn.setOnClickListener {
            setBgImageByResource(R.drawable.bg_light_blue)
        }
        mViewBinding.splitImageBtn.setOnClickListener {
            setBgImageByResource(R.drawable.bg_colorful)
        }
        setBgImageByResource(R.drawable.bg_splash)
    }

    private fun setBgImageByResource(imageResource: Int) {
        val bitmap = BitmapFactory.decodeResource(resources, imageResource)
        mViewBinding.bgImage.setImageBitmap(bitmap)
        detectBitmapColor(bitmap)
    }

    /**
     * 设置状态栏颜色
     */
    private fun setStatusBarColor(activity: Activity, statusColor: Int) {
        val window: Window = activity.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE)
        window.setStatusBarColor(statusColor)
        val mContentView = window.findViewById(Window.ID_ANDROID_CONTENT) as ViewGroup
        val mChildView = mContentView.getChildAt(0)
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false)
            ViewCompat.requestApplyInsets(mChildView)
        }
    }


    /**
     * 使用palette检测背景位图颜色，判断状态栏问题使用亮色还是暗色
     */
    private fun detectBitmapColor(bitmap: Bitmap) {
        val colorCount = 5
        val left = 0
        val top = 0
        val right = getScreenWidth()
        val bottom = getStatusBarHeight()

        Palette
            .from(bitmap)
            .maximumColorCount(colorCount)
            .setRegion(left, top, right, bottom)
            .generate {
                it?.let { palette ->
                    var mostPopularSwatch: Palette.Swatch? = null
                    for (swatch in palette.swatches) {
                        if (mostPopularSwatch == null
                            || swatch.population > mostPopularSwatch.population
                        ) {
                            mostPopularSwatch = swatch
                        }
                    }
                    mostPopularSwatch?.let { swatch ->
                        val luminance = ColorUtils.calculateLuminance(swatch.rgb)
                        // If the luminance value is lower than 0.5, we consider it as dark.
                        if (luminance < 0.5) {
                            setDarkStatusBar()
                        } else {
                            setLightStatusBar()
                        }
                    }
                }
            }
    }

    private fun setLightStatusBar() {
        val flags = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun setDarkStatusBar() {
        val flags = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }


    override val startObserve: StatusBarTestViewModel.() -> Unit = {

    }

}

class StatusBarTestViewModel(application: Application) : BaseViewModel(application) {

}