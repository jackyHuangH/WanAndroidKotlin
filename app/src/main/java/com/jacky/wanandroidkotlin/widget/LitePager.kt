package com.jacky.wanandroidkotlin.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.jacky.wanandroidkotlin.util.DisplayUtils
import q.rorbin.verticaltablayout.util.DisplayUtil

/**
 * @author:Hzj
 * @date  :2021/11/22
 * desc  ：仿网易云 歌单切换效果
 * record：
 */


class MyLayout : View {

    private val mPaint by lazy { Paint() }
    private lateinit var mCanvas: Canvas

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

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
        mCanvas = canvas
        val centerPoint = getCenterPoint()
        //绘制圆中心点
        mPaint.isAntiAlias = true
        mPaint.setColor(Color.parseColor("#0094ff"))
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = 10F
        canvas.drawCircle(centerPoint[0], centerPoint[1], 5F, mPaint)

        //绘制圆
        mPaint.setColor(Color.BLUE)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 15F
        val radius = context.resources.displayMetrics.widthPixels / 2 - 15
        canvas.drawCircle(centerPoint[0], centerPoint[1], radius.toFloat(), mPaint)
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
                mPaint.strokeWidth=20F
                //绘制三角起点
//                mCanvas.drawCircle(downX, downY, 15F, mPaint)
                mCanvas.drawPoint(downX,downY,mPaint)
                //绘制三角起始边
                drawLineToCenter(downX, downY, Color.GREEN)
                Log.d(TAG, "down: $downX , $downY")
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x
                val moveY = event.y
                Log.d(TAG, "move: $moveX , $moveY")
                //移动过程中绘制点
//                mPaint.setColor(Color.YELLOW)
//                mCanvas.drawCircle(moveX, moveY, 5F, mPaint)
            }
            MotionEvent.ACTION_UP -> {
                val upX = event.x
                val upY = event.y
                Log.d(TAG, "up: $upX , $upY")
                //绘制三角结束边
//                drawLineToCenter(upX, upY, Color.GREEN)
            }
            else -> {
                Log.d(TAG, "other")
            }
        }
        return super.onTouchEvent(event)
    }

    private fun drawLineToCenter(destX: Float, destY: Float, color: Int) {
        val center = getCenterPoint()
        mPaint.setColor(color)
        mCanvas.drawLine(center[0], center[1], destX, destY, mPaint)
    }
}