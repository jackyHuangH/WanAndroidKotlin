package com.jacky.wanandroidkotlin.widget

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.jacky.support.utils.DateFormatTemplate
import com.jacky.support.utils.LoggerKit
import com.jacky.support.utils.TimeUtils
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.model.entity.HourlyEntity
import com.jacky.wanandroidkotlin.ui.demos.WeatherResManager
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
    private val mDashLinePaint by lazy { Paint() }
    private val mBgPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val mBitmapPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private var mData: List<HourlyEntity>? = null
    private var mWidth = 0
    private var mHeight = 0

    //画虚线的点的索引index集合
    private lateinit var mDashLineIndexList: MutableList<Int>

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

    //底部阴影边界y值
    private var mBaseLineY = 0F

    //每个时段占用宽度，用来确定总宽度
    private val mItemWidth = DisplayUtils.dp2px(45).toFloat()
    private val mPaddingLeft = DisplayUtils.dp2px(10).toFloat()
    private val mPaddingRight = DisplayUtils.dp2px(10).toFloat()
    private val mPaddingTop = DisplayUtils.dp2px(10).toFloat()

    //给bitmap预留绘制区域
    private val mBitmapHeight = DisplayUtils.dp2px(30)

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
        //虚线画笔
        mDashLinePaint.apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, R.color.white_black)
            style = Paint.Style.STROKE
            strokeWidth = DisplayUtils.dp2px(1).toFloat()
            //设置虚线属性
            val dashLength = DisplayUtils.dp2px(3).toFloat()
            //intervals[]	间隔，用于控制虚线显示长度和隐藏长度，它必须为偶数(且至少为 2 个)，按照[显示长度，隐藏长度，显示长度，隐藏长度]的顺序来显示。
            //phase	相位(和正余弦函数中的相位类似，周期为intervals长度总和)，也可以简单的理解为偏移量。
            pathEffect = DashPathEffect(floatArrayOf(dashLength, dashLength), 0F)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            // 9.0以下需要关闭硬件加速,否则画虚线无效
            setLayerType(LAYER_TYPE_SOFTWARE, mDashLinePaint)
        }
        //图片画笔
        mBitmapPaint.apply {
            //api讲解：https://blog.csdn.net/qq_30889373/article/details/78800426
            //图像滤波
            //设置是否使用双线性过滤来绘制 Bitmap 。
            //图像在放大绘制的时候，默认使用的是最近邻插值过滤，这种算法简单，但会出现马赛克现象；而如果开启了双线性过滤，就可以让结果图像显得更加平滑。
            isFilterBitmap = true
            //抖动的原理和这个类似。所谓抖动（注意，它就叫抖动，不是防抖动，也不是去抖动，有些人在翻译的时候自作主张地加了一个「防」字或者「去」字，这是不对的），
            //是指把图像从较高色彩深度（即可用的颜色数）向较低色彩深度的区域绘制时，在图像中有意地插入噪点，通过有规律地扰乱图像来让图像对于肉眼更加真实的做法
            isDither = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        //计算总宽度
        val widthSize = (ITEM_SIZE * mItemWidth).toInt()
        mWidth = ((ITEM_SIZE - 1) * mItemWidth + mPaddingLeft + mPaddingRight).toInt()
        //可视宽度=屏幕宽度-内边距-外边距
        val visibleWidth =
            DisplayUtils.screenWidth() - DisplayUtils.dp2px(5) * 2 - DisplayUtils.dp2px(8) * 2
        //溢出屏幕的宽度
        mOverflowWidth = widthSize - visibleWidth
        Log.d(TAG, "测量宽高：$widthSize,$heightSize,溢出宽度：$mOverflowWidth")
        //计算可用高度
        mUsableHeight =
            (heightSize - mPaddingTop - mBitmapHeight - mTempTextHeight - mHourTextHeight).toInt()
        mHeight = measuredHeight
        //宽高未在xml中具体指定，需要重新测量
        setMeasuredDimension(widthSize, heightSize)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mData?.let { list ->
            //绘制温度折线
//            drawLine(canvas, list)
            //绘制平滑曲线
            drawCubicLine(canvas, list)

            val entity = list[mScrollIndex]
            val tempText = "${entity.temp}℃"
//            val tempX = mScrollIndex*mItemWidth
            val tempX = getScrollBarX()
            val tempY =
                mScale * (mMaxTemp - entity.temp) + mTempTextHeight * 1.3F - mPaddingTop
            //绘制顶部温度文字
            canvas.drawText(tempText, tempX, tempY, mTempPaint)
            //绘制底部天气图片
            drawWeatherBitmaps(canvas)
        }
    }

    /**
     * 绘制天气图片
     */
    private fun drawWeatherBitmaps(canvas: Canvas) {
        val scrollX = mScrollXOffset
        var leftHide: Boolean
        var rightHide: Boolean
        val screenWidth = DisplayUtils.screenWidth()
        LoggerKit.d("dashIndex:$mDashLineIndexList")
        for (i in 0 until mDashLineIndexList.size - 1) {
            val pointX = mDashLineIndexList[i]
            leftHide = true
            rightHide = true

            val left = mItemWidth * pointX + mPaddingLeft
            val right = mItemWidth * mDashLineIndexList[i + 1] + mPaddingLeft
            //绘制bitmap，图的中间位置
            var drawPoint = 0F
            if (left > scrollX && left < scrollX + screenWidth) {
                leftHide = false
            }
            if (right > scrollX && right < scrollX + screenWidth) {
                rightHide = false
            }
            if (!leftHide && !rightHide) {
                //左右边缘都显示
                drawPoint = (left + right) / 2F
            } else if (!leftHide && rightHide) {
                //左边缘显示，右边缘隐藏
                drawPoint = (left + scrollX + screenWidth) / 2F
            } else if (leftHide && !rightHide) {
                //右边缘显示，左边缘隐藏
                drawPoint = (scrollX + right) / 2F
            } else {
                //左右边缘都不显示
                if (left > scrollX + screenWidth || right < scrollX + screenWidth) {
                    continue
                } else {
                    drawPoint = scrollX + screenWidth / 2F
                }
            }

            val icon = mData?.get(pointX)?.icon
            val drawable = ContextCompat.getDrawable(context, WeatherResManager.getIconByCode(context, icon))
            val top = mBaseLineY - mBitmapHeight
            drawable?.let {
                val bitmapSize = DisplayUtils.dp2px(20).toFloat()
                val bitmap = WeatherResManager.getScaledBitmap(drawable, bitmapSize, bitmapSize)
                bitmap?.let {
                    //越界处理
                    if (drawPoint >= right - bitmap.width / 2) {
                        drawPoint = right - bitmap.width / 2
                    } else if (drawPoint <= left + bitmap.width / 2) {
                        drawPoint = left + bitmap.width / 2
                    }
                    Log.d(TAG, "drawPoint: $drawPoint")
                    canvas.save()
                    canvas.drawBitmap(bitmap, drawPoint - bitmap.width / 2, top, mBitmapPaint)
                    canvas.restore()
                }
            }
        }
    }

    /**
     * 绘制贝塞尔平滑曲线
     */
    private fun drawCubicLine(canvas: Canvas, list: List<HourlyEntity>) {
        //底部阴影边界y值
        mBaseLineY = (mHeight - mPaddingTop / 2).toFloat() - mHourTextHeight
        val path = Path()
        //记录虚线的x,y
        val dashWidth = mutableListOf<Float>()
        val dashHeight = mutableListOf<Float>()
        val pointList = mutableListOf<PointF>()
        list.forEachIndexed { index, entity ->
            val startX = mItemWidth * index + mPaddingLeft
            val startY =
                mScale * (mMaxTemp - entity.temp) + mTempTextHeight * 1.5F - mPaddingTop
            pointList.add(PointF(startX, startY))
            if (mDashLineIndexList.contains(index)) {
                dashWidth.add(startX)
                dashHeight.add(startY)
            }
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
            }

            // 更新值,
            prePreviousPointX = previousPointX
            prePreviousPointY = previousPointY
            previousPointX = currentPointX
            previousPointY = currentPointY
            currentPointX = nextPointX
            currentPointY = nextPointY

            //绘制底部小时文字
            canvas.drawText(hourText, pointList[index].x - mPaddingLeft, (mHeight - mPaddingTop / 2).toFloat(), mHourPaint)
        }
        //绘制曲线
        canvas.drawPath(path, mLinePaint)

        //绘制渐变阴影
        path.lineTo(mWidth - mPaddingRight, mBaseLineY)
        path.lineTo(mPaddingLeft, mBaseLineY)
        val colors = intArrayOf(
            ContextCompat.getColor(context, R.color.color_69B2F9),
            ContextCompat.getColor(context, R.color.color_300288d1),
            ContextCompat.getColor(context, R.color.color_18dfdfdf)
        )
        val linearGradient = LinearGradient(0F, 0F, 0F, height.toFloat(), colors, null, Shader.TileMode.CLAMP)
        mBgPaint.shader = linearGradient
        canvas.drawPath(path, mBgPaint)

        //绘制虚线
        if (dashHeight.isNotEmpty() && dashHeight.size > 1) {
            dashHeight.forEachIndexed { i, h ->
                if (i > 0 && i < dashHeight.size - 1) {
                    canvas.drawLine(dashWidth[i], h + 3, dashWidth[i], mBaseLineY, mDashLinePaint)
                }
            }
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
                mScale * (mMaxTemp - hourlyEntity.temp) + mTempTextHeight + mPaddingTop
            val endX = mItemWidth * (index + 1)
            if (index < list.size - 1) {
                //每个小时点间隔小时文本宽度一半
                val nextHour = list[index + 1]
                val endY =
                    mScale * (mMaxTemp - nextHour.temp) + mTempTextHeight + mPaddingTop
                canvas.drawLine(
                    startX + mHourTextWidth / 2, startY, endX + mHourTextWidth / 2, endY, mLinePaint
                )
            }
            //绘制底部小时文字
            canvas.drawText(
                hourText,
                startX,
                (mHeight - mPaddingTop / 2).toFloat(),
                mHourPaint
            )
        }
    }

    override fun onHorizontalScrolled(offset: Int) {
        mScrollXOffset = offset.toFloat()
        Log.d(TAG, "onHorizontalScrolled:$mScrollXOffset")
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
    fun initHourlyData(list: List<HourlyEntity>) {
        this.mData = list
        mDashLineIndexList = mutableListOf()
        var mPreIcon: Int = 0
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

            //计算画虚线的点索引,天气发生变化才记录一次索引
            if (mPreIcon != entity.icon) {
                mDashLineIndexList.add(index)
                mPreIcon = entity.icon
            }
        }
        //添加最后一条虚线索引
        mDashLineIndexList.add(list.size - 1)
        //温度极值
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