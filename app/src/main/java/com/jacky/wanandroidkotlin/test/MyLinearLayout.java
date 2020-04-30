package com.jacky.wanandroidkotlin.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author:Hzj
 * @date :2020/4/30
 * desc  ：自定义viewGroup-模仿LinearLayout
 * record：
 */
public class MyLinearLayout extends ViewGroup {

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //1.测量所有子view，触发每个子view的measure方法
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //2.计算每个子view位置和尺寸
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //得到viewgroup自己当前的可用空间
        int selfWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int selfHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int childCount = getChildCount();
        if (childCount > 0) {
            if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
                //如果宽高都是wrap_content
                //高度=子view高度相加，宽度=子view最宽宽度
                //3.保存自己的尺寸，
                setMeasuredDimension(getChildMaxWidth(), getChildTotalHeight());
            } else if (heightMode == MeasureSpec.AT_MOST) {
                //如果只有高度是wrap_content,宽度是具体数值
                setMeasuredDimension(selfWidthSize, getChildTotalHeight());
            } else if (widthMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(getChildMaxWidth(), selfHeightSize);
            }
        } else {
            // 没有子view
            setMeasuredDimension(0, 0);
        }
    }

    /**
     * 获取子view最大宽度
     */
    private int getChildMaxWidth() {
        int maxWidth = 0;
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                maxWidth = Math.max(maxWidth, getChildAt(i).getMeasuredWidth());
            }
        }
        return maxWidth;
    }

    /**
     * 获取子view高度总和
     */
    private int getChildTotalHeight() {
        int totalH = 0;
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                totalH += getChildAt(i).getMeasuredHeight();
            }
        }
        return totalH;
    }

    /**
     * 测量完后摆放子view，这里都是针对子view，相对于ViewGroup本身
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        //重置top=0
        int top = 0;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int measuredWidth = childAt.getMeasuredWidth();
            int measuredHeight = childAt.getMeasuredHeight();
            childAt.layout(l, top, l + measuredWidth, top + measuredHeight);
            top += measuredHeight;
        }
    }
}
