package com.jacky.wanandroidkotlin.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * @author:Hzj
 * @date  :2021/11/22
 * desc  ：仿网易云 歌单切换效果
 * record：
 */


class MyLayout : View {

    private val mPaint by lazy { Paint() }
    private val mCanvas by lazy { Canvas() }
    private var mDownX: Float = -1F
    private var mDownY: Float = -1F
    private var mUpX: Float = -1F
    private var mUpY: Float = -1F

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = 15F
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    companion object {
        const val TAG = "MyLayout"
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerPoint = getCenterPoint()
        //绘制圆中心点
        mPaint.color = Color.parseColor("#0094ff")
        canvas.drawCircle(centerPoint[0], centerPoint[1], 5F, mPaint)

        //绘制圆
        mPaint.setColor(Color.BLUE)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 15F
        val radius = context.resources.displayMetrics.widthPixels / 2 - 15
        canvas.drawCircle(centerPoint[0], centerPoint[1], radius.toFloat(), mPaint)

        if (mDownX >= 0 || mDownY >= 0) {
            //绘制点击的点
            mPaint.color = Color.RED
//            canvas?.drawCircle(downX, downY, 15F, mPaint)
            canvas.drawPoint(mDownX, mDownY, mPaint)
            //绘制三角起始边
            mPaint.setColor(Color.GREEN)
            canvas.drawLine(centerPoint[0], centerPoint[1], mDownX, mDownY, mPaint)
        }

        if (mUpX >= 0 || mUpY >= 0) {
            //绘制离开的点
            mPaint.color = Color.YELLOW
//            canvas?.drawCircle(downX, downY, 15F, mPaint)
            canvas.drawPoint(mUpX, mUpY, mPaint)
            //绘制三角结束边
            mPaint.setColor(Color.WHITE)
            canvas.drawLine(centerPoint[0], centerPoint[1], mUpX, mUpY, mPaint)
        }
    }

    private fun getCenterPoint(): FloatArray {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()
        return floatArrayOf(screenWidth / 2, screenHeight / 2)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val downX = event.x
                val downY = event.y
                //绘制三角起点
                //绘制三角起始边
                mDownX = downX
                mDownY = downY
                invalidate()
                Log.d(TAG, "down: $downX , $downY")
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x
                val moveY = event.y
                Log.d(TAG, "move: $moveX , $moveY")
                //移动过程中绘制点
                mUpX=moveX
                mUpY=moveY
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                val upX = event.x
                val upY = event.y
                Log.d(TAG, "up: $upX , $upY")
                //绘制三角结束边
                mUpX=upX
                mUpY=upY
                invalidate()
            }
            else -> {
                Log.d(TAG, "other")
            }
        }
        return super.onTouchEvent(event)
    }
}