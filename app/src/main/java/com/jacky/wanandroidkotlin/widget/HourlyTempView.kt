package com.jacky.wanandroidkotlin.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.jacky.support.utils.DateFormatTemplate
import com.jacky.support.utils.LoggerKit
import com.jacky.support.utils.TimeUtils
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.model.entity.HourlyEntity
import com.jacky.wanandroidkotlin.util.DisplayUtils
import kotlin.math.max
import kotlin.math.min

/**
 * @author:Hzj
 * @date  :2022/11/9
 * desc  ：每小时温度折线图
 * record：
 */
class HourlyTempView : View, HorizontalScrollWatcher {
    private val mLinePaint by lazy { Paint() }
    private val mTempPaint by lazy { Paint() }
    private val mHourPaint by lazy { Paint() }
    private var mData: List<HourlyEntity>? = null
    private var mHeight = 0

    //溢出屏幕的宽度
    private var mOverflowWidth = 0

    //滚动条最大滑动距离,默认非零，免得产生除零异常
    private var mBarMaxScrollOffset = 1

    //滚动条最大滑动距离
    private var mBarScrollOffset = 0
    private var mUsableHeight = 0

    //记录滑动的位置
    private var mScrollIndex = 0
    private var mScrollXOffset = 0F

    private var mTempTextHeight = 0
    private var mHourTextHeight = 0
    private var mHourTextWidth = 0
    private var mMaxTemp = 0
    private var mMinTemp = 0
    private var mTempDiff = 0
    private var mScale = 0F

    //每个时段占用宽度，用来确定总宽度
    private val mItemWidth = DisplayUtils.dp2px(45).toFloat()
    private val mPaddingTopBottom = DisplayUtils.dp2px(5)

    companion object {
        const val TAG = "HourlyView"
        const val ITEM_SIZE = 24
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    //初始化画笔
    private fun init() {
        mLinePaint.apply {
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            style = Paint.Style.STROKE
            strokeWidth = DisplayUtils.dp2px(1.5).toFloat()
            color = ContextCompat.getColor(context, R.color.color_AB82FF)
        }
        mTempPaint.apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, R.color.color_282828)
            textSize = DisplayUtils.sp2px(12).toFloat()
            mTempTextHeight = (fontMetrics.bottom - fontMetrics.top).toInt()
        }
        mHourPaint.apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, R.color.color_999999)
            textSize = DisplayUtils.sp2px(12).toFloat()
            mHourTextHeight = (fontMetrics.bottom - fontMetrics.top).toInt()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        //计算总宽度
        val widthSize = (ITEM_SIZE * mItemWidth).toInt()
        //可视宽度=屏幕宽度-内边距-外边距
        val visibleWidth =
            DisplayUtils.screenWidth() - DisplayUtils.dp2px(5) * 2 - DisplayUtils.dp2px(8) * 2
        //溢出屏幕的宽度
        mOverflowWidth = widthSize - visibleWidth
        LoggerKit.d("测量宽高：$widthSize,$heightSize,溢出宽度：$mOverflowWidth")
        //计算可用高度
        mUsableHeight =
            (heightSize - mPaddingTopBottom * 2 - mTempTextHeight - mHourTextHeight).toInt()
        mHeight = measuredHeight
        //宽高未在xml中具体指定，需要重新测量
        setMeasuredDimension(widthSize, heightSize)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制温度折线
        mData?.let { list ->
            val entity = list[mScrollIndex]
            val tempText = "${entity.temp}℃"
//            val tempX = mScrollIndex*mItemWidth
            val tempX = getScrollBarX()
            Log.d(TAG, "tempX:$tempX")
            val tempY =
                mScale * (mMaxTemp - entity.temp) + mPaddingTopBottom + mTempTextHeight / 2
            //绘制顶部温度文字
            canvas.drawText(tempText, tempX, tempY, mTempPaint)
            //绘制温度折线
//            drawLine(canvas, list)
            //绘制平滑曲线
            drawCubicLine(canvas, list)
        }
    }

    /**
     * 绘制贝塞尔平滑曲线
     */
    private fun drawCubicLine(canvas: Canvas, list: List<HourlyEntity>) {
        val path = Path()
        val pointList = mutableListOf<PointF>()
        list.forEachIndexed { index, entity ->
            val startX = mItemWidth * index
            val startY =
                mScale * (mMaxTemp - entity.temp) + mTempTextHeight + mPaddingTopBottom
            pointList.add(PointF(startX, startY))
        }

        var prePreviousPointX = Float.NaN
        var prePreviousPointY = Float.NaN
        var previousPointX = Float.NaN
        var previousPointY = Float.NaN
        var currentPointX = Float.NaN
        var currentPointY = Float.NaN
        var nextPointX = Float.NaN
        var nextPointY = Float.NaN
        list.forEachIndexed { index, hourlyEntity ->
            //当前点
            if (currentPointX.isNaN()) {
                currentPointX = pointList[index].x
                currentPointY = pointList[index].y
            }
            //计算上个点
            if (previousPointX.isNaN()) {
                if (index > 0) {
                    previousPointX = pointList[index - 1].x
                    previousPointY = pointList[index - 1].y
                } else {
                    //第一个点没有上个点，取当前点
                    previousPointX = currentPointX
                    previousPointY = currentPointY
                }
            }
            //计算上上个点
            if (prePreviousPointX.isNaN()) {
                if (index > 1) {
                    prePreviousPointX = pointList[index - 2].x
                    prePreviousPointY = pointList[index - 2].y
                } else {
                    //前2个点的上上个点，取上个点
                    prePreviousPointX = previousPointX
                    prePreviousPointY = previousPointY
                }
            }
            //计算下一个点
            if (index < list.size - 1) {
                nextPointX = pointList[index + 1].x
                nextPointY = pointList[index + 1].y
            } else {
                //最后一个点的下一个点，取当前点
                nextPointX = currentPointX
                nextPointY = currentPointY
            }
            val hourText = getHourTime(hourlyEntity)

            if (index == 0) {
                //第一个点，移动到起始位置
                path.moveTo(currentPointX, currentPointY)
            } else {
                //控制点，用3阶贝塞尔实现平滑曲线
                //计算当前点和上上个点差值
                val firstDiffX = currentPointX - prePreviousPointX
                val firstDiffY = currentPointY - prePreviousPointY
                val secondDiffX = nextPointX - previousPointX
                val secondDiffY = nextPointY - previousPointY
                val control1X = previousPointX + (0.2F * firstDiffX)
                val control1Y = previousPointY + (0.2F * firstDiffY)
                val control2X = currentPointX - (0.2F * secondDiffX)
                val control2Y = currentPointY - (0.2F * secondDiffY)
                path.cubicTo(control1X, control1Y, control2X, control2Y, currentPointX, currentPointY)
                canvas.drawPath(path, mLinePaint)
            }

            // 更新值,
            prePreviousPointX = previousPointX
            prePreviousPointY = previousPointY
            previousPointX = currentPointX
            previousPointY = currentPointY
            currentPointX = nextPointX
            currentPointY = nextPointY

            //绘制底部小时文字
            canvas.drawText(hourText, pointList[index].x, (mHeight - mPaddingTopBottom / 2).toFloat(), mHourPaint)
        }
    }

    /**
     * 绘制折线，起伏过渡不够平滑
     */
    private fun drawLine(canvas: Canvas, list: List<HourlyEntity>) {
        list.forEachIndexed { index, hourlyEntity ->
            val hourText = getHourTime(hourlyEntity)
            val startX = mItemWidth * index
            val startY =
                mScale * (mMaxTemp - hourlyEntity.temp) + mTempTextHeight + mPaddingTopBottom
            val endX = mItemWidth * (index + 1)
            if (index < list.size - 1) {
                //每个小时点间隔小时文本宽度一半
                val nextHour = list[index + 1]
                val endY =
                    mScale * (mMaxTemp - nextHour.temp) + mTempTextHeight + mPaddingTopBottom
                canvas.drawLine(
                    startX + mHourTextWidth / 2, startY, endX + mHourTextWidth / 2, endY, mLinePaint
                )
            }
            //绘制底部小时文字
            canvas.drawText(
                hourText,
                startX,
                (mHeight - mPaddingTopBottom / 2).toFloat(),
                mHourPaint
            )
        }
    }

    override fun onHorizontalScrolled(offset: Int) {
        mScrollXOffset = offset.toFloat()
        Log.d(TAG, "offset:$mScrollXOffset")
        //根据横向移动偏移量计算移动的位置
        mScrollIndex = ((offset / mOverflowWidth.toFloat()) * ITEM_SIZE).toInt()
        //横向移动温度文字
        if (mScrollIndex < 0) {
            mScrollIndex = 0
        } else if (mScrollIndex > ITEM_SIZE - 1) {
            mScrollIndex = ITEM_SIZE - 1
        }
        invalidate()
    }

    /**
     * 设置最大滑动范围
     */
    fun setScrollRange(barOffset: Int, barMaxScrollOffset: Int) {
        this.mBarScrollOffset = barOffset
        this.mBarMaxScrollOffset = barMaxScrollOffset
        Log.d(TAG, "bar最大滑动距离：$barMaxScrollOffset,bar滑动距离：$barOffset")
        invalidate()
    }

    /**
     * 设置24小时天气数据
     */
    fun setHourlyData(list: List<HourlyEntity>) {
        this.mData = list
        list.forEachIndexed { index, entity ->
            if (index == 0) {
                mMaxTemp = entity.temp
                mMinTemp = entity.temp
                //计算小时文本宽度
                val hourText = getHourTime(entity)
                mHourTextWidth = mHourPaint.measureText(hourText).toInt()
            }
            //计算最大和最小温度
            mMaxTemp = max(entity.temp, mMaxTemp)
            mMinTemp = min(entity.temp, mMinTemp)
        }
        mTempDiff = mMaxTemp - mMinTemp
        //高度和温差比例值
        if (mUsableHeight != 0) {
            mScale = mUsableHeight / mTempDiff.toFloat()
        }
        invalidate()
    }

    /**
     * 计算当前滚动条x位置,用于动态改变温度文字位置
     */
    private fun getScrollBarX(): Float {
        //此处要考虑到文字不超过左右两边边界，故总宽度=(itemsize-1)*itemwidth
        return mItemWidth * (ITEM_SIZE - 1) * mBarScrollOffset / mBarMaxScrollOffset
    }

    /**
     * 格式化小时时间
     */
    private fun getHourTime(entity: HourlyEntity): String {
        return TimeUtils.getDateByFormat(entity.fxTime.time, DateFormatTemplate.hm)
    }
}