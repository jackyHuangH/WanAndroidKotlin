package com.jacky.wanandroidkotlin.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.HorizontalScrollView

/**
 * @author:Hzj
 * @date  :2022/11/11
 * desc  ：自定义HorizontalScrollView，内部只能包含一个子view，监听滚动偏移量，设置内部小时温度文字偏移
 * record：
 */
class IndexHorizontalScrollView : HorizontalScrollView {
    private var mOnlyChildView: HourlyTempView? = null
    // 记录上次的offset, 防止父子互相刷新
    private var mPreOffset = -1

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        initChildView()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //计算最大可滑动距离
        val scrollOffset = computeHorizontalScrollOffset()
        val scrollRange = computeHorizontalScrollRange()
        val maxOffset = scrollRange - measuredWidth
        if (maxOffset != 0 && mPreOffset != scrollOffset) {
            mPreOffset = scrollOffset
            mOnlyChildView?.setScrollRange(scrollOffset, maxOffset)
        }
    }

    private fun initChildView() {
        val childAt = getChildAt(0)
        if (childAt == null || childAt !is HorizontalScrollWatcher) {
            throw IllegalStateException("请确保有且只有一个子view，子view是HorizontalScrollWatcher的实现类！")
        }
        mOnlyChildView = childAt as? HourlyTempView
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        (mOnlyChildView as? HorizontalScrollWatcher)?.onHorizontalScrolled(l)
    }
}

/**
 * 定义横向滚动偏移量监听接口
 */
interface HorizontalScrollWatcher {
    fun onHorizontalScrolled(offset: Int)
}