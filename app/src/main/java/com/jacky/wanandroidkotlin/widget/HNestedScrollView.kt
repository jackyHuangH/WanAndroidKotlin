package com.jacky.wanandroidkotlin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.jacky.wanandroidkotlin.util.log

/**
 * @author:Hzj
 * @date  :2022/9/2
 * desc  ：自定义处理NestedScrollView 与 子view嵌套滑动
 * record：
 */
class HNestedScrollView : NestedScrollView {
    private val mFlingHelper by lazy { FlingHelper(context) }

    /**
     * 顶部的View
     */
    private var mTopView: View? = null

    /**
     * 底部的content View
     */
    private var mContentView: ViewGroup? = null

    /**
     * 用于判断RecyclerView是否在fling
     */
    var mIsRecyclerViewStartFling = false

    /**
     * 记录当前自身已经滑动的Y距离
     */
    var mTotalDy = 0

    /**
     * 记录当前滑动的y轴加速度
     */
    private var mVelocityY = 0

    /**
     * 向外部提供滑动监听
     */
    var mHScrollListener: ((v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) -> Unit)? =
        null


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        doInit()
    }

    private fun doInit() {
        //添加滑动监听
        setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (mIsRecyclerViewStartFling) {
                mTotalDy = 0
                mIsRecyclerViewStartFling = false
            }
            log("scrollY:$scrollY")
            if (scrollY == 0) {
                log("top scroll")
            }
            //滑动topView到顶部不可见时
            if (scrollY >= (mTopView?.measuredHeight ?: 0)) {
                log("Bottom scroll v.measuredHeight:${v.measuredHeight}")
                //滑动到底部以后，还有惯性让子类继续滑动
                dispatchChildFling()
            } else {
                //todo 子view向下滑动后，父类继续消费剩余滑动距离
                val dy=scrollY-oldScrollY
                if (dy<0){

                }
            }
            //在contentView fling情况下，记录当前contentView在y轴的偏移
            mTotalDy += scrollY - oldScrollY
            mHScrollListener?.invoke(v, scrollX, scrollY, oldScrollX, oldScrollY)
        }
    }

    private fun dispatchChildFling() {
        if (mVelocityY != 0) {
            //将惯性加速度转为具体的距离
            val splineFlingDistance = mFlingHelper.getSplineFlingDistance(mVelocityY)
            //fling滑动的距离若外层HNestedScrollView未全部消费，剩余距离则交给contentView滑动
            if (splineFlingDistance > mTotalDy) {
                val velocity =
                    mFlingHelper.getVelocityByDistance(splineFlingDistance - mTotalDy.toDouble())
                mContentView?.let {
                    val rv = getChildRecyclerView(it) as? RecyclerView
                    rv?.fling(0, velocity)
                }
            }
        }
        //重置变量
        mTotalDy = 0
        mVelocityY = 0
    }

    /**
     * 递归 找到可以滑动的bottom 子view
     */
    private fun getChildRecyclerView(viewGroup: ViewGroup): ViewGroup? {
        val childCount = viewGroup.childCount
        if (childCount > 0) {
            for (i in 0 until viewGroup.childCount) {
                val view = viewGroup.getChildAt(i)
                if (view is RecyclerView && view.javaClass == RecyclerView::class.java) {
                    return viewGroup.getChildAt(i) as RecyclerView
                } else if (viewGroup.getChildAt(i) is ViewGroup) {
                    val childRecyclerView: ViewGroup? =
                        getChildRecyclerView(viewGroup.getChildAt(i) as ViewGroup)
                    if (childRecyclerView is RecyclerView) {
                        return childRecyclerView
                    }
                }
                continue
            }
        }
        return null
    }

    override fun fling(velocityY: Int) {
        super.fling(velocityY)
        //给变量mVelocityY赋值
        if (velocityY <= 0) {
            this.mVelocityY = 0
        } else {
            this.mVelocityY = velocityY
            this.mIsRecyclerViewStartFling = true
        }
    }

    /**
     * View加载完成后，获取top，bottomContent
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        mTopView = (getChildAt(0) as? ViewGroup)?.getChildAt(0)
        mContentView = (getChildAt(0) as? ViewGroup)?.getChildAt(1) as? ViewGroup
    }

    /**
     * 参数	解释
     * target	触发嵌套滑动的 view
     * dx	表示 view 本次 x 方向的滚动的总距离，单位：像素
     * dy	表示 view 本次 y 方向的滚动的总距离，单位：像素
     * consumed	输出：表示父布局消费的水平和垂直距离。
     * type	触发滑动事件的类型：其值有
     * ViewCompat. TYPE_TOUCH
     * ViewCompat. TYPE_NON_TOUCH
     *
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(target, dx, dy, consumed, type)
        log("dy:$dy")
        //如果能继续向上滑动，就滑动
        val canScrollUp = dy > 0 && scrollY < (mTopView?.measuredHeight ?: 0)
        if (canScrollUp) {
            scrollBy(0, dy)
            consumed[1] = dy
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // getMeasuredHeight() 得到的就是屏幕高度 当前ZSNestedScrollView高度是MatchParent
        log("onMeasure getMeasuredHeight() = $measuredHeight")
        val lp = mContentView?.layoutParams
        lp?.height = measuredHeight
        // 调整contentView的高度为屏幕高度，这样ZSNestedScrollView总高度就是屏幕高度+topView的高度
        // 因此往上滑动 滑完topView后,TabLayout就卡在顶部了，因为ZSNestedScrollView滑不动了啊，就这么高
        // 接着在滑就是其内部的RecyclerView去滑动了
        mContentView?.layoutParams = lp
    }

}

/**
 * Fling处理工具类
 */
class FlingHelper(context: Context) {
    private fun getSplineDeceleration(i: Int): Double {
        return Math.log(
            (0.35f * Math.abs(i)
                .toFloat() / (mFlingFriction * mPhysicalCoeff)).toDouble()
        )
    }

    private fun getSplineDecelerationByDistance(d: Double): Double {
        return (DECELERATION_RATE.toDouble() - 1.0) * Math.log(d / (mFlingFriction * mPhysicalCoeff).toDouble()) / DECELERATION_RATE.toDouble()
    }

    fun getSplineFlingDistance(i: Int): Double {
        return Math.exp(getSplineDeceleration(i) * (DECELERATION_RATE.toDouble() / (DECELERATION_RATE.toDouble() - 1.0))) * (mFlingFriction * mPhysicalCoeff).toDouble()
    }

    fun getVelocityByDistance(d: Double): Int {
        return Math.abs((Math.exp(getSplineDecelerationByDistance(d)) * mFlingFriction.toDouble() * mPhysicalCoeff.toDouble() / 0.3499999940395355).toInt())
    }

    companion object {
        private val DECELERATION_RATE = (Math.log(0.78) / Math.log(0.9)).toFloat()
        private val mFlingFriction = ViewConfiguration.getScrollFriction()
        private var mPhysicalCoeff: Float = 0.0f
    }

    init {
        mPhysicalCoeff = context.resources.displayMetrics.density * 160.0f * 386.0878f * 0.84f
    }

}