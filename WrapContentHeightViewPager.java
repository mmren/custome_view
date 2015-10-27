package com.zd.myd.custome_view;

/**
 * Created by renmingming on 15/9/17.
 */
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class WrapContentHeightViewPager extends ViewPager {

    public WrapContentHeightViewPager(Context context) {
        super(context);
    }

    public WrapContentHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize_h = MeasureSpec.getSize(heightMeasureSpec);
        int specSize_w = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        //下面遍历所有child的高度
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int h = 0 ;
            if(child.getLayoutParams() != null){
                h =child.getLayoutParams().height;
            }
            height = h > height ? h : height;
        }

        switch (specMode){
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                height = specSize_h;

                break;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}