package com.jacky.wanandroidkotlin.test

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.jacky.wanandroidkotlin.util.DisplayUtils

/**
 * @author:Hzj
 * @date  :2022/11/17
 * desc  ：贝塞尔二阶曲线，由起始点，终点，控制点组成
 * record：
 */
class BezierView : View {
    private var mCenterX = 0
    private var mCenterY = 0

    //起始点
    private var mStart: PointF = PointF()
    private var mEnd: PointF = PointF()

    //控制点1
    private var mControl1: PointF = PointF()

    //控制点2
    private var mControl2: PointF = PointF()

    //提供外界设置当前控制点1
    private var mCurrentControlPoint = 1

    //辅助线画笔
    private val mLinePaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    //曲线画笔
    private val mCubicPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

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

    private fun init() {
        mLinePaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = DisplayUtils.dp2px(2).toFloat()
        }
        mCubicPaint.apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = DisplayUtils.dp2px(3).toFloat()
        }
    }

    /**
     * 设置当前控制点
     */
    fun setCurrentControlPoint(@androidx.annotation.IntRange(from = 1, to = 2) control: Int) {
        this.mCurrentControlPoint = control
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2
        mCenterY = h / 2
        //确定起始点和终点位置
        mStart = PointF(mCenterX - 200F, mCenterY.toFloat())
        mEnd = PointF(mCenterX + 200F, mCenterY.toFloat())
        mControl1 = PointF(mCenterX.toFloat()-50F, mCenterY - 50F)
        mControl2 = PointF(mCenterX.toFloat()+50F, mCenterY - 50F)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            //触摸屏幕改变控制点坐标
            if (mCurrentControlPoint == 1) {
                mControl1.x = event.x
                mControl1.y = event.y
            }else{
                mControl2.x = event.x
                mControl2.y = event.y
            }
            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制起始点和终点
        val dotRadius = DisplayUtils.dp2px(3).toFloat()
        mLinePaint.style = Paint.Style.FILL
        canvas.drawCircle(mStart.x, mStart.y, dotRadius, mLinePaint)
        canvas.drawCircle(mEnd.x, mEnd.y, dotRadius, mLinePaint)
        canvas.drawCircle(mControl1.x, mControl1.y, dotRadius, mLinePaint)
        canvas.drawCircle(mControl2.x, mControl2.y, dotRadius, mLinePaint)
        //绘制辅助线
        mLinePaint.style = Paint.Style.STROKE
        canvas.drawLine(mStart.x, mStart.y, mControl1.x, mControl1.y, mLinePaint)
        canvas.drawLine(mControl1.x, mControl1.y, mControl2.x, mControl2.y, mLinePaint)
        canvas.drawLine(mEnd.x, mEnd.y, mControl2.x, mControl2.y, mLinePaint)
        val path = Path()
        //移动path到起始点
        path.moveTo(mStart.x, mStart.y)
        //绘制贝塞尔二阶曲线
//        path.quadTo(mControl1.x, mControl1.y, mEnd.x, mEnd.y)
        //绘制贝塞尔三阶曲线
        path.cubicTo(mControl1.x,mControl1.y,mControl2.x,mControl2.y,mEnd.x,mEnd.y)
        canvas.drawPath(path, mCubicPaint)
    }
}