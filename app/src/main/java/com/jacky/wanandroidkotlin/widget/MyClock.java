package com.jacky.wanandroidkotlin.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.jacky.wanandroidkotlin.R;

import java.util.Calendar;

/**
 * 时钟 自定义view
 */
public class MyClock extends View {
    private Paint paint;
    private int hours, minute, seconds;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                getTime();
                invalidate();
            }
        }
    };

    public MyClock(Context context) {
        super(context);
    }

    public MyClock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        getTime();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyClock);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(8);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 20, paint);

        paint.setStrokeWidth(4);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 30, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 10, paint);

        for (int i = 1; i <= 12; i++) {
            canvas.save();
            canvas.rotate(30 * i, getWidth() / 2, getHeight() / 2);
            canvas.drawLine(getWidth() / 2, 40, getWidth() / 2, 60, paint);
            canvas.restore();
        }

        paint.setStrokeWidth(8);
        canvas.save();
        canvas.rotate(30 * hours + 0.5f * minute, getWidth() / 2, getHeight() / 2);
        canvas.drawLine(getWidth() / 2, getHeight() / 2, getWidth() / 2, getHeight() / 2 - getHeight() / 5, paint);
        canvas.restore();

        paint.setStrokeWidth(5);
        canvas.save();
        canvas.rotate(6 * minute, getWidth() / 2, getHeight() / 2);
        canvas.drawLine(getWidth() / 2, getHeight() / 2, getWidth() / 2, getHeight() / 2 - getHeight() / 4, paint);
        canvas.restore();

        paint.setStrokeWidth(3);
        paint.setColor(Color.RED);
        canvas.save();
        canvas.rotate(6 * seconds, getWidth() / 2, getHeight() / 2);
        canvas.drawLine(getWidth() / 2, getHeight() / 2, getWidth() / 2, getHeight() / 2 - getHeight() / 3, paint);
        canvas.restore();
        mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    void getTime() {
        Calendar calendar = Calendar.getInstance();
        hours = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        seconds = calendar.get(Calendar.SECOND);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int wmode = MeasureSpec.getMode(widthMeasureSpec);
        int hmode = MeasureSpec.getMode(heightMeasureSpec);

        int wsize = MeasureSpec.getSize(widthMeasureSpec);
        int hsize = MeasureSpec.getSize(heightMeasureSpec);

        int size = 300;

        if (wmode == MeasureSpec.EXACTLY) {
            if (hmode == MeasureSpec.EXACTLY) {
                size = Math.min(wsize, hsize);
            } else {
                size = wsize;
            }
        } else {
            if (hmode == MeasureSpec.EXACTLY) {
                size = hsize;
            }
        }

        setMeasuredDimension(size, size);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }
}
