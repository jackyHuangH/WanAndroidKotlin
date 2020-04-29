package com.jacky.wanandroidkotlin.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * @author:Hzj
 * @date :2020/4/29
 * desc  ： ViewGroup自定义练习-正方形
 * record：
 */
public class SquareView extends ViewGroup {
    //默认尺寸
    private static final int DEF_SIZE = 100;

    public SquareView(Context context) {
        super(context);
    }

    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = getSquareSize(DEF_SIZE, widthMeasureSpec);
        int heightSize = getSquareSize(DEF_SIZE, heightMeasureSpec);
        if (widthSize > heightSize) {
            widthSize = heightSize;
        } else {
            heightSize = widthSize;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    private int getSquareSize(int defSize, int measureSpec) {
        int result = 0;
        //取测量模式
        int mode = MeasureSpec.getMode(measureSpec);
        //取测量大小
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                result = defSize;
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                result = size;
                break;
            default:
                result = size;
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
