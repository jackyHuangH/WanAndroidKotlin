package com.jacky.wanandroidkotlin.widget

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.util.DisplayUtils
import com.jacky.wanandroidkotlin.util.setOnAntiShakeClickListener
import com.jacky.wanandroidkotlin.wrapper.orNotNullNotEmpty
import kotlinx.android.synthetic.main.layout_float_play.view.*
import kotlin.math.absoluteValue

/**
 * @author:Hzj
 * @date  :2020/12/10
 * desc  ：自定义悬浮播放控件
 * record：
 */
class FloatPlayLayout : LinearLayout {

    /**
     * 是否已展开
     */
    private var mHasExpanded = false

    //记录手指触摸位置
    private var mDownY = 0F

    companion object {
        const val TAG = "FloatPlay"
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.layout_float_play, this).apply {
            iv_music_pic.setOnAntiShakeClickListener {
                //收缩状态进行展开动画
                startAnim()
                mHasExpanded = mHasExpanded.not()
            }
            iv_shrink.setOnAntiShakeClickListener {
                //展开状态收缩
                if (mHasExpanded) {
                    startAnim()
                    mHasExpanded = false
                }
            }
        }
        gravity = Gravity.CENTER
    }

    private fun startAnim() {
        //这个平滑动画用animateLayoutChanges属性配合View.Gone即可实现
//        ll_control.visibility = if (mHasExpanded) View.GONE else View.VISIBLE
        //content应该展示的宽度
        val contentWidth = DisplayUtils.dp2px(180f)
        val animator = if (mHasExpanded) {
            ValueAnimator.ofInt(contentWidth, 0)
        } else {
            ValueAnimator.ofInt(0, contentWidth)
        }
        animator.duration = 400
        animator.interpolator = if (mHasExpanded) {
            BounceInterpolator()
        } else {
            OvershootInterpolator()
        }
        animator.addUpdateListener {
            //平滑的动态设置整体宽度
            val animateWidth = it.animatedValue as Int
            val lp = ll_control.layoutParams.apply {
                width = animateWidth
            }
            ll_control.layoutParams = lp
        }
        animator.start()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownY = event.y
                Log.d(TAG, "downY:$mDownY")
            }
            MotionEvent.ACTION_MOVE -> {
                //move动作时拦截触摸事件,避免与点击事件冲突，设置最小移动阈值10像素
                return (event.y - mDownY).absoluteValue > 10
            }
            MotionEvent.ACTION_UP -> {
            }
            else -> {
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
                var offsetY = translationY + (event.y - mDownY)
                //因为translationY不管处于ViewGroup什么位置，初始值都为0，所以要-top
                //最小移动距离,向下为正值
                val minOffsetY = 0
                //最大移动距离，向上为负值
                val maxOffsetY = -(top - DisplayUtils.dp2px(10))
                if (offsetY > minOffsetY) {
                    offsetY = minOffsetY.toFloat()
                }
                if (offsetY < maxOffsetY) {
                    offsetY = maxOffsetY.toFloat()
                }
                Log.d(TAG, "offsetY:$offsetY")
                translationY = offsetY
                return true
            }
            MotionEvent.ACTION_UP -> {
                val upY = event.y
                Log.d(TAG, "upY:$upY")
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    //是否点击在指定view范围内
    private fun touchInRangeOfView(view: View, ev: MotionEvent): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]
        return !(ev.x < x || ev.x > x + view.width || ev.y < y || ev.y > y + view.height)
    }

    //==========================public api ============================

    /**
     * 播放按钮点击事件
     */
    fun onPlayClick(click: (View) -> Unit) {
        iv_play.setOnAntiShakeClickListener {
            click.invoke(it)
        }
    }

    /**
     * 悬浮窗点击事件
     */
    fun onFloatPlayClick(click: (View) -> Unit) {
        ll_float_root.setOnAntiShakeClickListener {
            click.invoke(it)
        }
    }

    fun updateMusicName(name: String?) {
        tv_song_name.text = name.orNotNullNotEmpty("暂无曲目")
    }
}