package com.zd.myd.custome_view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zd.myd.R;
/**
 * Created by renmingming on 15/9/11.
 */
public class TiltTextview extends TextView
{

//    private int width;
//
//    private int height;

    private int mRotateAngle;

    private float rx;

    private float ry;

    public TiltTextview(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        final Resources res = getResources();
        float density = context.getResources().getDisplayMetrics().density;
        if (attrs != null)
        {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TiltTextview);
            mRotateAngle = a.getInt(R.styleable.TiltTextview_rotateAngle, mRotateAngle);
            rx = a.getFloat(R.styleable.TiltTextview_rotationX, rx);
            ry = a.getFloat(R.styleable.TiltTextview_rotationY, ry);
        }
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
//    {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
////        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
//        width = getMeasuredWidth();
//        height= getMeasuredHeight();
//        int h=0,w =0;
//        if (width > 0 && mRotateAngle != 0 && h ==0 )
//        {
//                    h = (int)(
//                            (Math.abs(
//                                    Math.sin(mRotateAngle * Math.PI / 180)
//                                            * width
//                                    ))
//                                    +
//                            (Math.abs(
//                                    Math.sin(mRotateAngle * Math.PI / 180)
//                                            * height
//                                    ))
//                            );
//            setMeasuredDimension(h, h);
//        }
//
//    }

    @Override
    protected void onDraw(Canvas canvas)
    {
//        canvas.save();
        canvas.rotate(mRotateAngle, (int) (getMeasuredWidth() * rx), (int) (getMeasuredHeight() * ry));
        super.onDraw(canvas);
//        canvas.restore();
    }
}
