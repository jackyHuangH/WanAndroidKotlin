package com.jacky.wanandroidkotlin.jetpack.binding

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.Animation.INFINITE
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.util.ResolveUtils
import com.jacky.wanandroidkotlin.widget.FloatPlayLayout
import com.jacky.wanandroidkotlin.wrapper.loadBlurTrans
import com.jacky.wanandroidkotlin.wrapper.loadCircle
import com.jacky.wanandroidkotlin.wrapper.musicplay.PlayerStatus

/**
 * @author:Hzj
 * @date  :2020/8/28
 * desc  ：dataBinding 提供了 BindingAdapter 这个注解用于支持自定义属性，或者是修改原有属性。
 * 注解值可以是已有的 xml 属性，例如 android:src、android:text等，也可以自定义属性然后在 xml 中使用
 * record：
 */

/**
 * 为EditText绑定输入监听
 */
@BindingAdapter(value = ["addTextWatcher"], requireAll = true)
fun addTextWatcher(editText: EditText, textWatcher: TextWatcher) {
    editText.addTextChangedListener(textWatcher)
}

/**
 * imageView设置本地图片资源
 */
@BindingAdapter(value = ["imgSrc"])
fun imgSrcRes(imageView: ImageView, resId: Int) {
    imageView.setImageResource(resId)
}

/**
 * imageView设置是否选中
 */
@BindingAdapter(value = ["imgSelected"])
fun imgSelected(imageView: ImageView, selected: Boolean) {
    imageView.isSelected = selected
}

/**
 * 悬浮播放栏 播放/暂停按钮 imageView设置是否选中
 */
@BindingAdapter(value = ["floatPlayControlSelected"])
fun floatPlayControl(floatPlayLayout: FloatPlayLayout, selected: Boolean) {
    floatPlayLayout.setPlayControlSelected(selected)
}

/**
 * 悬浮播放栏 更新歌曲名字
 */
@BindingAdapter(value = ["floatMusicName"])
fun floatMusicName(floatPlayLayout: FloatPlayLayout, name: String?) {
    floatPlayLayout.updateMusicName(name)
}

/**
 * 悬浮播放栏 imageView本地圆形图片
 */
@BindingAdapter(value = ["floatImgCircleUri"])
fun floatImageCircleUri(floatPlayLayout: FloatPlayLayout, albumId: Long) {
    floatPlayLayout.updateMusicAlbum(albumId)
}

/**
 * imageView本地圆形图片
 */
@BindingAdapter(value = ["imgCircleUri"])
fun imgCircleUri(imageView: ImageView, albumId: Long) {
    if (albumId <= 0) {
        imageView.setImageResource(R.drawable.play_album_default)
        return
    }
    val uri = ResolveUtils.albumUriById(albumId)
    imageView.loadCircle(
        imageView.context, uri,
        R.drawable.play_album_default
    )
}

/**
 * imageView本地图片高斯模糊
 */
@BindingAdapter(value = ["imgBlurUri"])
fun imgBlurUri(imageView: ImageView, albumId: Long) {
    if (albumId <= 0) {
        imageView.setImageResource(0)
        return
    }
    imageView.loadBlurTrans(
        imageView.context,
        ResolveUtils.albumUriById(albumId),
        90,
        R.drawable.play_album_default
    )
}


object AnimBinding {
    //    private var rotateAnimator: ObjectAnimator? = null
    private var rotateAnimator: ValueAnimator? = null

    /**
     * View旋转动画
     */
    @BindingAdapter(value = ["rotateAnim"])
    @JvmStatic
    fun rotateAnimate(view: View, playStatus: Int) {
        Log.d("Rotate", "playStatus:$playStatus")
        if (rotateAnimator == null) {
            rotateAnimator = ObjectAnimator.ofFloat(view, "rotation", 0F, 360F).apply {
                repeatCount = INFINITE
                duration = 20000
                interpolator = LinearInterpolator()
            }
            /*//ValueAnimator实现方式
            rotateAnimator = ValueAnimator.ofFloat(0F, 360F).apply {
                repeatCount = INFINITE
                duration = 20000
                interpolator = LinearInterpolator()
                addUpdateListener {
                    view.rotation = it.animatedValue as Float
                }
            }*/
        }
        when (playStatus) {
            PlayerStatus.PLAY_START, PlayerStatus.PLAY_RESUME -> {
                if (rotateAnimator?.isPaused == true) {
                    rotateAnimator?.resume()
                } else {
                    rotateAnimator?.start()
                }
            }
            PlayerStatus.PLAY_PAUSE -> {
                rotateAnimator?.pause()
            }
            PlayerStatus.PLAY_RESET -> {
                //重置状态，不处理
            }
        }
    }

    //页面销毁时调用回收动画
    fun releaseAnim() {
        rotateAnimator?.cancel()
        rotateAnimator = null
    }
}
