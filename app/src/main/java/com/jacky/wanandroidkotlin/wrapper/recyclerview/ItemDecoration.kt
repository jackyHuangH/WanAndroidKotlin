package com.jacky.wanandroidkotlin.wrapper.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.zenchn.support.kit.AndroidKit
import org.jetbrains.annotations.NotNull

/**
 * recyclerView 垂直分割线
 */
class SpaceItemDecoration(space: Int) : RecyclerView.ItemDecoration() {

    private val mSpace = space

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = 0
        outRect.bottom = mSpace
    }
}

/**
 * 作   者：wangr on 2019/12/5 9:36
 * 描   述：分隔线
 * 修订记录：
 */

open class BaseItemDecoration(@DividerModeScope protected val mode: Int) : RecyclerView.ItemDecoration()

internal fun RecyclerView.getSpanCount(): Int {
    // 列数
    return when (val layoutManager = layoutManager) {
        is GridLayoutManager -> layoutManager.spanCount
        is StaggeredGridLayoutManager -> layoutManager.spanCount
        else -> -1
    }
}

internal fun BaseItemDecoration.isLastColumn(parent: RecyclerView, pos: Int, totalCount: Int): Boolean {
    val layoutManager = parent.layoutManager
    if (layoutManager is LinearLayoutManager) {
        return LinearLayoutManager.VERTICAL == layoutManager.orientation && pos >= totalCount - 1
    } else if (layoutManager is GridLayoutManager) {
        if ((pos + 1) % parent.getSpanCount() == 0) {
            return true// 如果是最后一列，则不需要绘制右边
        }
    } else if (layoutManager is StaggeredGridLayoutManager) {
        if (layoutManager.orientation == StaggeredGridLayoutManager.VERTICAL) {
            if ((pos + 1) % parent.getSpanCount() == 0) {
                return true   // 如果是最后一列，则不需要绘制右边
            }
        } else {
            val count = totalCount - totalCount % parent.getSpanCount()
            if (pos >= count) {
                return true  // 如果是最后一列，则不需要绘制右边
            }
        }
    }
    return false
}

internal fun BaseItemDecoration.isLastRaw(parent: RecyclerView, pos: Int, totalCount: Int): Boolean {
    val layoutManager = parent.layoutManager
    if (layoutManager is LinearLayoutManager) {
        return LinearLayoutManager.VERTICAL == layoutManager.orientation && pos >= totalCount - 1
    } else if (layoutManager is GridLayoutManager) {
        val count = totalCount - totalCount % parent.getSpanCount()
        if (pos >= count) {
            return true // 如果是最后一行，则不需要绘制底部
        }
    } else if (layoutManager is StaggeredGridLayoutManager) {
        // StaggeredGridLayoutManager 且纵向滚动
        if (layoutManager.orientation == StaggeredGridLayoutManager.VERTICAL) {
            val count = totalCount - totalCount % parent.getSpanCount()
            // 如果是最后一行，则不需要绘制底部
            if (pos >= count) {
                return true
            }
        } else {
            // StaggeredGridLayoutManager 且横向滚动
            // 如果是最后一行，则不需要绘制底部
            if ((pos + 1) % parent.getSpanCount() == 0) {
                return true
            }
        }
    }
    return false
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.CONSTRUCTOR)
@IntDef(DividerMode.Auto, DividerMode.Raw, DividerMode.Column)
annotation class DividerModeScope

object DividerMode {
    const val Auto = 0//自动
    const val Raw = 1//纵向列表->横向
    const val Column = 2//横向列表->纵向
}

class DrawableDecoration(@NotNull private val drawable: Drawable, @DividerModeScope mode: Int) : BaseItemDecoration(mode) {

    constructor(context: Context, @DividerModeScope mode: Int = DividerMode.Auto) : this(
        drawable = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider)).run {
            val divider = getDrawable(0)
            recycle()
            divider ?: throw IllegalStateException()
        },
        mode = mode
    )

    constructor(context: Context, @DrawableRes drawableRes: Int, @DividerModeScope mode: Int = DividerMode.Auto) : this(
        drawable = context.resources.getDrawable(drawableRes, null),
        mode = mode
    )

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (parent.childCount > 1) {
            when (mode) {
                DividerMode.Auto -> {
                    if (parent.layoutManager is LinearLayoutManager) {
                        if (LinearLayoutManager.VERTICAL == (parent.layoutManager as LinearLayoutManager).orientation) {
                            drawRaw(c, parent)
                        } else {
                            drawColumn(c, parent)
                        }
                    } else {
                        drawRaw(c, parent)
                        drawColumn(c, parent)
                    }
                }
                DividerMode.Column -> drawRaw(c, parent)
                DividerMode.Raw -> drawColumn(c, parent)
            }
        }
    }

    private fun drawRaw(c: Canvas, parent: RecyclerView) {
        c.save()
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + drawable.intrinsicHeight
            drawable.setBounds(left, top, right, bottom)
            drawable.draw(c)
        }
        c.restore()
    }

    private fun drawColumn(c: Canvas, parent: RecyclerView) {
        c.save()
        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom
        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + drawable.intrinsicHeight
            drawable.setBounds(left, top, right, bottom)
            drawable.draw(c)
        }
        c.restore()
    }
}

class EdgeDecoration(
    @IntRange(from = 0) left: Int = 0,
    @IntRange(from = 0) top: Int = 0,
    @IntRange(from = 0) right: Int = 0,
    @IntRange(from = 0) bottom: Int = 0,
    dp2px: Boolean = false
) : RecyclerView.ItemDecoration() {

    private val left: Int = if (dp2px) AndroidKit.Dimens.dp2px(left) else left
    private val top: Int = if (dp2px) AndroidKit.Dimens.dp2px(top) else top
    private val right: Int = if (dp2px) AndroidKit.Dimens.dp2px(right) else right
    private val bottom: Int = if (dp2px) AndroidKit.Dimens.dp2px(bottom) else bottom

    constructor(@IntRange(from = 0) size: Int, gravity: Int = Gravity.BOTTOM, dp2px: Boolean = false) : this(
        left = if ((Gravity.START == gravity || Gravity.LEFT == gravity)) size else 0,
        top = if (Gravity.TOP == gravity) size else 0,
        right = if ((Gravity.END == gravity || Gravity.RIGHT == gravity)) size else 0,
        bottom = if (Gravity.BOTTOM == gravity) size else 0,
        dp2px = dp2px
    )

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        when (val layoutManager = parent.layoutManager) {
            is LinearLayoutManager -> {
                val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                val totalCount = parent.adapter?.itemCount ?: 0
                if (itemPosition <= totalCount) {// 如果是最后一行，则不需要绘制底部
                    outRect.set(left, top, right, bottom)
                } else {
                    if (LinearLayoutManager.VERTICAL == layoutManager.orientation) {
                        outRect.set(left, top, right, 0)
                    } else {
                        outRect.set(left, top, 0, bottom)
                    }
                }
            }
            else -> {
                outRect.set(left, top, right, bottom)
            }
        }

    }
}