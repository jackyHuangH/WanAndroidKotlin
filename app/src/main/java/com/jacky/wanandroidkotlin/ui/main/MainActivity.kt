package com.jacky.wanandroidkotlin.ui.main

import android.support.v4.widget.DrawerLayout
import android.view.View
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.common.Constant
import com.jacky.wanandroidkotlin.util.StatusBarUtil
import com.jacky.wanandroidkotlin.wrapper.glide.GlideApp
import com.zenchn.support.widget.CircleTextImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), DrawerLayout.DrawerListener {

    override fun getLayoutRes() = R.layout.activity_main

    override fun initWidget() {
        drawer_layout.addDrawerListener(this)
        val headerView = navigation.getHeaderView(0)
        val circleTextImageView = headerView.findViewById(R.id.civ_user) as CircleTextImageView
        GlideApp
            .with(this)
            .load(Constant.imgUrls[1])
            .override(800,800)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(circleTextImageView)
        resetNvHeader()
    }

    private fun resetNvHeader() {
        StatusBarUtil.setPaddingSmart(this, ll_title)
        StatusBarUtil.setPaddingSmart(this, navigation)

//        val statusHeight = ImmersionBar.getStatusBarHeight(this)
//        val llParam = ll_title.layoutParams as LinearLayout.LayoutParams
//        llParam.topMargin = statusHeight
//        ll_title.layoutParams = llParam
//
//        val nvParam = navigation.layoutParams as DrawerLayout.LayoutParams
//        nvParam.topMargin = statusHeight
//        navigation.layoutParams = nvParam
    }

    override fun onDrawerStateChanged(p0: Int) {
    }

    override fun onDrawerSlide(p0: View, p1: Float) {
    }

    override fun onDrawerClosed(p0: View) {
    }

    override fun onDrawerOpened(p0: View) {
    }

}
