package com.jacky.wanandroidkotlin.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

/**
 * @author:Hzj
 * @date  :2022/8/30
 * desc  ：NestedScrollView 处理与bottomSheetBehavior联动滑动的效果，参照NestedRecyclerView实现
 * record：效果不太顺畅，仅供参考
 */
@Deprecated("不够顺畅，有问题")
open class SNBNestedScrollView : NestedScrollView {
    var scrollByOutsideView = false
    protected var bottomSheetBehavior: SNBBottomSheetBehavior<View>? = null
    private var placeholderView: View? = null


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        //监听底层scrollview滑动，滑动到底部时并继续上滑时，调用setSlideHeight方法，改变behavior状态，
        // setSlideHeight 会调用子 View 的 requestLayout 从而触发 onLayoutChild 回调，我们在 onLayoutChild 中改变子 View 的位置
        setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (!scrollByOutsideView) {
                bottomSheetBehavior?.let {
                    it.setSlideByOutsideView(isPlaceholderViewVisible())
                    it.setOutsideView(this)
                    //计算滑动距离
                    val slideHeight =
                        measuredHeight - (placeholderView!!.top - scrollY) + it.peekHeight
                    if (isPlaceholderViewVisible()) {
                        it.slideHeight = slideHeight
                    }
                }
            }
            scrollByOutsideView = false
        }
    }


    /**
     * 绑定SNBBottomSheetBehavior
     */
    fun bindSNBBottomSheetBehavior(bottomSheetBehavior: SNBBottomSheetBehavior<View>) {
        if (this.bottomSheetBehavior != null) {
            return
        }

        this.bottomSheetBehavior = bottomSheetBehavior
        placeholderView = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                bottomSheetBehavior.getCollapsedOffset()
            )
        }
        val firstChildView = getChildAt(0) as ViewGroup
        firstChildView.addView(placeholderView)
    }

    /**
     * placehoder是否可见
     */
    private fun isPlaceholderViewVisible(): Boolean {
        if (placeholderView == null) {
            return false
        }

        val scrollBounds = Rect()
        getHitRect(scrollBounds)
        return placeholderView?.getLocalVisibleRect(scrollBounds) ?: false
    }
}


/**
 * RecyclerView 处理与bottomSheetBehavior联动滑动的效果
 */
@Deprecated("不够顺畅，有问题")
open class NestedRecycleView : RecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    var scrollByOutsideView = false
    protected var bottomSheetBehavior: SNBBottomSheetBehavior<View>? = null
    private var placeholderView: View? = null
    private var totalDy: Int = 0

    init {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalDy += dy
                if (!scrollByOutsideView) {
                    bottomSheetBehavior?.let {
                        val isPlaceholderViewVisible = isPlaceholderViewVisible()
                        it.setSlideByOutsideView(isPlaceholderViewVisible)
                        it.setOutsideView(this@NestedRecycleView)

                        val slideHeight =
                            measuredHeight - (placeholderView!!.top - totalDy) + it.peekHeight

                        if (isPlaceholderViewVisible) {
                            it.slideHeight = slideHeight
                        }
                    }
                }

                scrollByOutsideView = false
            }
        })
    }

    fun bindSNBBottomSheetBehavior(bottomSheetBehavior: SNBBottomSheetBehavior<View>) {
        if (this.bottomSheetBehavior != null) {
            return
        }

        this.bottomSheetBehavior = bottomSheetBehavior
        placeholderView = View(context)
        placeholderView?.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            bottomSheetBehavior.getCollapsedOffset()
        )
        val firstChildView = getChildAt(0) as ViewGroup
        firstChildView.addView(placeholderView)
    }

    private fun isPlaceholderViewVisible(): Boolean {
        if (placeholderView == null) {
            return false
        }

        val scrollBounds = Rect()
        getHitRect(scrollBounds)
        return placeholderView!!.getLocalVisibleRect(scrollBounds)
    }

}