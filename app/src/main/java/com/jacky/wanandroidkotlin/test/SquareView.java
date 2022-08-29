package com.jacky.wanandroidkotlin.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author:Hzj
 * @date :2020/4/29
 * desc  ： ViewGroup自定义练习-正方形
 * record：
 */
public class SquareView extends View {
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
        // 获取宽-测量规则的模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        // 获取高-测量规则的模式和大小
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT && lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = DEF_SIZE;
            height = DEF_SIZE;
        } else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = DEF_SIZE;
            height = heightSize;
        } else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = widthSize;
            height = DEF_SIZE;
        }
        setMeasuredDimension(width, height);
    }

    private int getSquareSize(int defSize, int measureSpec) {
        int result = 0;
        //取测量模式
        int mode = MeasureSpec.getMode(measureSpec);
        //取测量大小
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.UNSPECIFIED) {
            result = defSize;
        } else if (mode == MeasureSpec.EXACTLY || mode == MeasureSpec.AT_MOST) {
            result = size;
        }
        return result;
    }
}
