package com.jacky.wanandroidkotlin.wrapper.glide

import android.content.Context
import android.widget.ImageView
import com.jacky.wanandroidkotlin.R
import com.youth.banner.loader.ImageLoader

/**
 * @author:Hzj
 * @date  :2018/12/14/014
 * desc  ：youth banner imageLoader
 * record：
 */
class GlideBannerImageLoader : ImageLoader() {

    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        GlideApp
                .with(context)
                .load(path)
                .placeholder(R.drawable.holder_banner)
                .into(imageView)
    }
}