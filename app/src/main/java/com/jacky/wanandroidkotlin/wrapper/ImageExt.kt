package com.jacky.wanandroidkotlin.wrapper

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.jacky.wanandroidkotlin.R
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * des 图片加载扩展方法
 *
 */


/**
 * 通过url加载
 */
fun ImageView.loadUrl(context: Context, url: String) {
    Glide.with(context)
        .load(url)
        .transition(withCrossFade())
        .into(this)
}

/**
 * 通过uri加载
 */
fun ImageView.loadUri(context: Context, uri: Uri) {
    Glide.with(context)
        .load(uri)
        .transition(withCrossFade())
        .into(this)
}

/**
 * 高斯模糊加渐入渐出
 */
fun ImageView.loadBlurTrans(
    context: Context,
    uri: Uri,
    radius: Int,
    @DrawableRes fallbackResId: Int
) {
    Glide.with(context)
        .load(uri)
        .fallback(fallbackResId)
        .thumbnail(0.1f)
        .skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .apply(RequestOptions.bitmapTransform(BlurTransformation(radius,5)))
        .transition(withCrossFade(400))
        .into(this)
}


/**
 * 圆形图片
 */
fun ImageView.loadCircle(context: Context, uri: Uri, @DrawableRes fallbackResId: Int) {
    Glide.with(context)
        .load(uri)
        .fallback(fallbackResId)
        .error(fallbackResId)
        .apply(RequestOptions.bitmapTransform(CircleCrop()))
        .into(this)
}

/**
 * 圆角图片
 */
fun ImageView.loadRadius(context: Context, url: String, radius: Int = 20) {
    Glide.with(context)
        .load(url)
        .centerCrop()
        .error(R.drawable.pic_default)
        .transition(withCrossFade())
        .transform(RoundedCorners(radius))
        .into(this)
}


