package com.jacky.wanandroidkotlin.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withSave
import com.google.android.material.bottomsheet.BottomSheetBehavior

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

        //canvas 快照(save)和回滚(restore),用于保存和恢复canvas状态
        canvas.save()

        //移动坐标中心点到屏幕中心
        canvas.translate(centerPoint[0], centerPoint[1])
        //绘制圆中心点
        mPaint.color = Color.parseColor("#0094ff")
        canvas.drawCircle(0F, 0F, 5F, mPaint)

        //绘制圆
        mPaint.setColor(Color.WHITE)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 10F
        val radiusOuter = (context.resources.displayMetrics.widthPixels / 2 - 15).toFloat()
        val radiusInner = radiusOuter - 30F
        canvas.drawCircle(0F, 0F, radiusOuter, mPaint)
        canvas.drawCircle(0F, 0F, radiusInner, mPaint)
        //使用canvas.translate,scale
        for (degree in 0..60) {
            canvas.drawLine(0F, radiusInner, 0F, radiusOuter, mPaint)
            canvas.rotate(6F)
        }

        if (mDownX >= 0 || mDownY >= 0) {
            //绘制点击的点
            mPaint.color = Color.RED
            //由于canvas中心点从默认左上角移动到屏幕中心了，所以触摸点要经过换算
            val touchX = mDownX - centerPoint[0]
            val touchY = mDownY - centerPoint[1]
//            canvas?.drawCircle(downX, downY, 15F, mPaint)
            canvas.drawPoint(touchX, touchY, mPaint)
            //绘制三角起始边
            mPaint.setColor(Color.GREEN)
            canvas.drawLine(0F, 0F, touchX, touchY, mPaint)
        }

        //恢复屏幕左上角坐标原点
        canvas.restore()

        if (mUpX >= 0 || mUpY >= 0) {
            //绘制离开的点
            mPaint.color = Color.YELLOW
            //由于canvas中心点从默认左上角移动到屏幕中心了，所以离开点要经过换算
//            val leaveX = mUpX - centerPoint[0]
//            val leaveY = mUpY - centerPoint[1]
//            canvas.drawPoint(leaveX, leaveY, mPaint)
            //绘制三角结束边
//            mPaint.setColor(Color.WHITE)
//            canvas.drawLine(0F, 0F, leaveX, leaveY, mPaint)

            //restore后的坐标点绘制
            val leaveX = mUpX
            val leaveY = mUpY
            mPaint.style=Paint.Style.FILL
            canvas.drawCircle(leaveX, leaveY, 15F, mPaint)
            //绘制三角结束边
            mPaint.setColor(Color.WHITE)
            canvas.drawLine(centerPoint[0], centerPoint[1], leaveX, leaveY, mPaint)
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
                mUpX = moveX
                mUpY = moveY
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                val upX = event.x
                val upY = event.y
                Log.d(TAG, "up: $upX , $upY")
                //绘制三角结束边
                mUpX = upX
                mUpY = upY
                invalidate()
            }
            else -> {
                Log.d(TAG, "other")
            }
        }
        return super.onTouchEvent(event)
    }
}