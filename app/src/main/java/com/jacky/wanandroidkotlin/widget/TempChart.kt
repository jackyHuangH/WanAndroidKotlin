package com.jacky.wanandroidkotlin.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.model.entity.DailyEntity
import com.jacky.wanandroidkotlin.util.DisplayUtils

/**
 * @author:Hzj
 * @date  :2022/10/31
 * desc  ：天气温度折线自定义view
 * record：
 */
class TempChart : View {
    private val mDayPaint by lazy { Paint() }
    private val mNightPaint by lazy { Paint() }
    private val mTextPaint by lazy { Paint() }

    //前一日天气数据
    private var mPrevious: DailyEntity? = null

    //当日天气
    private lateinit var mDaily: DailyEntity

    //后一日天气
    private var mNext: DailyEntity? = null

    //15天内最高温度，用于绘制折线点最高点
    private var mMaxTemp: Int = 0

    //15天内最低温度，用于绘制折线点最低点
    private var mMinTemp: Int = 0

    //宽度的一半
    private var mHalfWidth = 0F

    //高度
    private var mHeight = 0F

    //可用高度
    private var mUsableHeight = 0

    //文本高度
    private var mTextHeight = 0

    //高温度文本宽度
    private var mHighTextWidth = 0

    //低温度文本宽度
    private var mLowTextWidth = 0
    private var mLowText = ""
    private var mHighText = ""

    //高度/温度 比例
    private var mHeightTempScale = 0F

    //最高温度和最低温度差值
    private var mTempDiff = 0

    //上下内边距
    private val mTopBottomPadding = DisplayUtils.dp2px(8)

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initPaint()
    }

    //初始化画笔
    private fun initPaint() {
        val lineWidth = DisplayUtils.dp2px(2).toFloat()
        mDayPaint.color = ContextCompat.getColor(context, R.color.color_FBA603)
        mDayPaint.isAntiAlias = true
        mDayPaint.strokeWidth = lineWidth
        //设置线帽，防止连线处断层
        mDayPaint.strokeCap = Paint.Cap.SQUARE
        mNightPaint.color = ContextCompat.getColor(context, R.color.color_0288d1)
        mNightPaint.isAntiAlias = true
        mNightPaint.strokeWidth = lineWidth
        mNightPaint.strokeCap = Paint.Cap.SQUARE
        //计算温度差，设置合适高度
        mTextPaint.color = ContextCompat.getColor(context, R.color.color_999999)
        mTextPaint.isAntiAlias = true
        mTextPaint.textSize = DisplayUtils.sp2px(12).toFloat()
        mTextHeight = (mTextPaint.fontMetrics.bottom - mTextPaint.fontMetrics.top).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHalfWidth = measuredWidth / 2f
        mHeight = measuredHeight.toFloat()
        //计算折线和点可用高度=总高度-文本高度*2
        mUsableHeight = (mHeight - mTopBottomPadding * 2 - mTextHeight * 2).toInt()
        //计算高度和温度差比例
        mHeightTempScale = mUsableHeight / mTempDiff.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //移动画布到横向中间位置
        canvas.translate(mHalfWidth, 0F)
        //计算最大值和最小值差，确定绘制点位置
        val cyDay = mHeightTempScale * (mMaxTemp - mDaily.tempMax) + mTextHeight + mTopBottomPadding
        canvas.drawCircle(0F, cyDay, 8F, mDayPaint)
        val cyNight =
            mHeightTempScale * (mMaxTemp - mDaily.tempMin) + mTextHeight + mTopBottomPadding
        canvas.drawCircle(0F, cyNight, 8F, mNightPaint)
        //绘制温度文本
        canvas.drawText(mHighText, -mHighTextWidth / 2F, cyDay - mTextHeight / 2, mTextPaint)
        canvas.drawText(mLowText, -mLowTextWidth / 2F, cyNight + mTextHeight, mTextPaint)
        //绘制折线连接前一天和后一天
        mPrevious?.let { previous ->
            //确定前一天的点位置
            val cyPreviousDay =
                mHeightTempScale * (mMaxTemp - previous.tempMax) + mTextHeight + mTopBottomPadding
            val cyPreviousNight =
                mHeightTempScale * (mMaxTemp - previous.tempMin) + mTextHeight + mTopBottomPadding
            canvas.drawLine(-mHalfWidth, (cyPreviousDay + cyDay) / 2, 0F, cyDay, mDayPaint)
            canvas.drawLine(-mHalfWidth, (cyPreviousNight + cyNight) / 2, 0F, cyNight, mNightPaint)
        }
        mNext?.let { next ->
            //确定后一天的点位置
            val cyNextDay =
                mHeightTempScale * (mMaxTemp - next.tempMax) + mTextHeight + mTopBottomPadding
            val cyNextNight =
                mHeightTempScale * (mMaxTemp - next.tempMin) + mTextHeight + mTopBottomPadding
            canvas.drawLine(0F, cyDay, mHalfWidth, (cyNextDay + cyDay) / 2, mDayPaint)
            canvas.drawLine(0F, cyNight, mHalfWidth, (cyNextNight + cyNight) / 2, mNightPaint)
        }
    }


    /**
     * 设置天气数据
     */
    fun setDailyWeatherData(
        dailyEntity: DailyEntity,
        previous: DailyEntity?,
        next: DailyEntity?,
        minTemp: Int,
        maxTemp: Int
    ) {
        mPrevious = previous
        mDaily = dailyEntity
        mNext = next
        mMinTemp = minTemp
        mMaxTemp = maxTemp
        mLowText = "${dailyEntity.tempMin}℃"
        mHighText = "${dailyEntity.tempMax}℃"
        mTempDiff = mMaxTemp - mMinTemp
        //测量文本宽度
        mHighTextWidth = mTextPaint.measureText(mHighText).toInt()
        mLowTextWidth = mTextPaint.measureText(mLowText).toInt()
    }
}