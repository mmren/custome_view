/*******************************************************************************
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2013 Triggertrap Ltd
 * Author Neil Davies
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.zd.myd.custome_view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.zd.myd.R;

/**
 * SeekArc.java
 * <p/>
 * This is a class that functions much like a SeekBar but
 * follows a circle path instead of a straight line.
 *
 * @author ren mingming
 */
public class SeekArc extends View
{

    private static final String TAG = SeekArc.class.getSimpleName();

    private static int INVALID_PROGRESS_VALUE = -1;

    // The initial rotational offset -90 means we start at 12 o'clock
    private final int mAngleOffset = -90;

    /**
     * The Drawable for the seek arc thumbnail
     */
    private Drawable mThumb;

    /**
     * The Maximum value that this SeekArc can be set to
     */
    private int mMax = 100;


    /**
     * The Maximum value that this SeekArc can be set to
     */
    private int mMin = 0;


    /**
     * The Current value that the SeekArc is set to
     */
    private int mProgress = 0;

    /**
     * The width of the progress line for this SeekArc
     */
    private int mProgressWidth = 4;

    /**
     * The Width of the background arc for the SeekArc
     */
    private int mArcWidth = 2;

    /**
     * The Angle to start drawing this Arc from
     */
    private int mStartAngle = 0;

    /**
     * The Angle through which to draw the arc (Max is 360)
     */
    private int mSweepAngle = 360;

    /**
     * The rotation of the SeekArc- 0 is twelve o'clock
     */
    private int mRotation = 0;

    /**
     * Give the SeekArc rounded edges
     */
    private boolean mRoundedEdges = false;

    /**
     * Enable touch inside the SeekArc
     */
    private boolean mTouchInside = true;

    /**
     * Will the progress increase clockwise or anti-clockwise
     */
    private boolean mClockwise = true;

    // Internal variables
    private int mArcRadius = 0;

    private float mProgressSweep = 0;

    private RectF mArcRect = new RectF();

    private Paint mArcPaint;

    private Paint mProgressPaint;

    private Paint mTextPaint;

    private int mTranslateX;

    private int mTranslateY;

    private int mThumbXPos;

    private int mThumbYPos;

    private double mTouchAngle;

    private float mTouchIgnoreRadius;

    private OnSeekArcChangeListener mOnSeekArcChangeListener;

    private int mSegs = 2;

    private int mTextdriverHigh = 10;

    private int mArcColor;

    private int mProgressColor;

    private int mTextColor;

    private int mTextSize = 20;

    private int mThumbYPos_f;

    private int mStep = 1;

    private float mMoveX;

    private float mMoveY;

    private float mStartX;

    private float mStartY;

    private boolean isdebug = true;

    public interface OnSeekArcChangeListener
    {

        /**
         * Notification that the progress level has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically.
         *
         * @param seekArc  The SeekArc whose progress has changed
         * @param progress The current progress level. This will be in the range
         *                 0..max where max was set by
         *                 {@link #setmMax(int)}. (The default value for
         *                 max is 100.)
         * @param fromUser True if the progress change was initiated by the user.
         */
        void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser);

        /**
         * Notification that the user has started a touch gesture. Clients may
         * want to use this to disable advancing the seekbar.
         *
         * @param seekArc The SeekArc in which the touch gesture began
         */
        void onStartTrackingTouch(SeekArc seekArc);

        /**
         * Notification that the user has finished a touch gesture. Clients may
         * want to use this to re-enable advancing the seekarc.
         *
         * @param seekArc The SeekArc in which the touch gesture began
         */
        void onStopTrackingTouch(SeekArc seekArc);
    }

    public SeekArc(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public SeekArc(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, R.attr.seekArcStyle);
    }

    public SeekArc(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle)
    {
        if(isdebug)
        Log.d(TAG, "Initialising SeekArc");
        final Resources res = getResources();
        float density = context.getResources().getDisplayMetrics().density;

        // Defaults, may need to link this into theme settings
        mArcColor = res.getColor(R.color.progress_gray);
        mProgressColor = res.getColor(android.R.color.holo_blue_light);
        mTextColor = res.getColor(android.R.color.holo_red_dark);
        int thumbHalfheight = 0;
        int thumbHalfWidth = 0;
        mThumb = res.getDrawable(R.drawable.seek_arc_control_selector);
        // Convert progress width to pixels for current density
        mProgressWidth = (int) (mProgressWidth * density);


        if (attrs != null)
        {
            // Attribute initialization
            final TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.SeekArc, defStyle, 0);

            Drawable thumb = a.getDrawable(R.styleable.SeekArc_thumb);
            if (thumb != null)
            {
                mThumb = thumb;
            }


            thumbHalfheight = mThumb.getIntrinsicHeight() / 2;
            thumbHalfWidth = mThumb.getIntrinsicWidth() / 2;
            mThumb.setBounds(-thumbHalfWidth, -thumbHalfheight, thumbHalfWidth,
                    thumbHalfheight);

            mMax = a.getInteger(R.styleable.SeekArc_max, mMax);
            mMin = a.getInteger(R.styleable.SeekArc_min, mMin);
            mProgress = a.getInteger(R.styleable.SeekArc_progress, mProgress);
            mProgressWidth = (int) a.getDimension(R.styleable.SeekArc_progressWidth, mProgressWidth);
            mArcWidth = (int) a.getDimension(R.styleable.SeekArc_arcWidth, mArcWidth);
            mStartAngle = a.getInt(R.styleable.SeekArc_startAngle, mStartAngle);
            mSweepAngle = a.getInt(R.styleable.SeekArc_sweepAngle, mSweepAngle);
            mRotation = a.getInt(R.styleable.SeekArc_rotation, mRotation);
            mRoundedEdges = a.getBoolean(R.styleable.SeekArc_roundEdges, mRoundedEdges);
            mTouchInside = a.getBoolean(R.styleable.SeekArc_touchInside, mTouchInside);
            mClockwise = a.getBoolean(R.styleable.SeekArc_clockwise, mClockwise);
            mSegs = a.getInt(R.styleable.SeekArc_segs, mSegs);
            mTextdriverHigh = a.getInt(R.styleable.SeekArc_textdriverHigha, mTextdriverHigh);
            mArcColor = a.getColor(R.styleable.SeekArc_arcColor, mArcColor);
            mTextColor = a.getColor(R.styleable.SeekArc_textColor, mTextColor);
            mProgressColor = a.getColor(R.styleable.SeekArc_progressColor, mProgressColor);
            mTextSize = a.getDimensionPixelSize(R.styleable.SeekArc_textSize, mTextSize);
            mStep = a.getInt(R.styleable.SeekArc_step, mStep);
            a.recycle();
        }

        mProgress = (mProgress > mMax) ? mMax : mProgress;
        mProgress = (mProgress < mMin) ? mMin : mProgress;

        mSweepAngle = (mSweepAngle > 360) ? 360 : mSweepAngle;
        mSweepAngle = (mSweepAngle < 0) ? 0 : mSweepAngle;

        mStartAngle = (mStartAngle > 360) ? 0 : mStartAngle;
        mStartAngle = (mStartAngle < 0) ? 0 : mStartAngle;

        mArcPaint = new Paint();
        mArcPaint.setColor(mArcColor);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);
        //mArcPaint.setAlpha(45);

        mProgressPaint = new Paint();
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);

        if (mRoundedEdges)
        {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        mProgressSweep = (float) mProgress * mSweepAngle / (mMax - mMin);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        if (!mClockwise)
        {
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
        }

        // Draw the arcs
        final int arcStart = mStartAngle + mAngleOffset + mRotation;
        final int arcSweep = mSweepAngle;
        canvas.drawArc(mArcRect, arcStart, arcSweep, false, mArcPaint);
        int angle_seg = arcSweep / mSegs;
        if(isdebug)
        Log.i(TAG, "=============onDraw " + mProgressSweep);
        canvas.drawArc(mArcRect, arcStart, mProgressSweep, false, mProgressPaint);

        // Draw the thumb nail
        int ty = mTranslateY + getPaddingTop() - mThumbYPos;
        if (mArcRect.top <= (mArcWidth / 2 + mTextdriverHigh + mTextSize + getPaddingTop()))
        {
            ty = mArcWidth / 2 + mTextdriverHigh + mTextSize + getPaddingTop() + mArcRadius - mThumbYPos;
        }

        canvas.translate(mTranslateX - mThumbXPos, ty);
        mThumb.draw(canvas);

        for (int i = 0; i < mSegs + 1; i++)
        {
            int angle = mStartAngle + i * angle_seg + mRotation + 90;
            int x = (int) (mThumbXPos - mArcRadius * Math.cos(angle * Math.PI / 180));


            int y = (int) (mThumbYPos - (Math.sin(angle * Math.PI / 180) * mArcRadius));
            String str = String.valueOf(mMin + (mMax - mMin) * i / mSegs);
            canvas.save();
            //文字宽度
            float textLength = mTextPaint.measureText(str);

//			y = (y+mThumbYPos - mThumbYPos) < mArcWidth/2+mTextdriverHigh ? mArcWidth/2+mTextdriverHigh :y ;
//			y = (y+mThumbYPos - mThumbYPos) < mArcWidth/2+mTextdriverHigh ? mArcWidth/2+mTextdriverHigh :y ;
            canvas.rotate(angle - 90, x, y);
            if (i > 0 && i < mSegs + 1)
            {
                x = x - (int) (textLength / 2);
            }
            if (i == mSegs + 1)
            {
                x = x + (int) (textLength);
            }
            canvas.drawText(str, x, y - (mArcWidth / 2 + mTextdriverHigh), mTextPaint);
            canvas.restore();
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {

        final int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
//		final int min = Math.min(width, height);
        final int min = width;
        float top = 0;
        float left = 0;


        mTranslateX = (int) (width * 0.5f);
        mTranslateY = (int) (height * 0.5f);

        int mArcDiameter = min - getPaddingLeft();
        mArcRadius = mArcDiameter / 2;
//		top = height / 2 - (mArcDiameter / 2);
        top = mArcWidth / 2 + mTextdriverHigh + mTextSize;
        left = width / 2 - (mArcDiameter / 2);
        top = top < (mArcWidth / 2 + mTextdriverHigh + mTextSize) ? (mArcWidth / 2 + mTextdriverHigh + mTextSize) : top;
        top = top + getPaddingTop();
        mArcRect.set(left, top, left + mArcDiameter, top + mArcDiameter);
        setTouchInSide(mTouchInside);
        int arcStart = (int) mProgressSweep + mStartAngle + mRotation + 90;
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(arcStart)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(arcStart)));

        mThumbYPos_f = (int) (mArcRadius * Math.sin(Math.toRadians(mStartAngle + mRotation + 90)));
        int y = mArcRadius - mThumbYPos_f + mArcWidth + mTextdriverHigh + mTextSize;
        mTranslateY = mTranslateY < mTranslateX ? mTranslateX : mTranslateY;
        if(isdebug)
        Log.i(TAG, "onMeasure ==============" + y);
        setMeasuredDimension(width, y + getPaddingTop() + getPaddingBottom());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        boolean canMove = false;


        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getRawX();
                mStartY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:

                mMoveX = Math.abs(event.getRawX() - mStartX);
                mMoveY = Math.abs(event.getRawY() - mStartY);
                if (mMoveX > mMoveY && mMoveX>30){
                    canMove = true;
                }
                if (canMove)
                {
                    updateOnTouch(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (canMove)
                {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (canMove)
                {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                mStartX = 0;
                mStartY = 0;
                break;
        }

        return true;
    }

    @Override
    protected void drawableStateChanged()
    {
        super.drawableStateChanged();
        if (mThumb != null && mThumb.isStateful())
        {
            int[] state = getDrawableState();
            mThumb.setState(state);
        }
        invalidate();
    }

    private void onStartTrackingTouch()
    {
        if (mOnSeekArcChangeListener != null)
        {
            mOnSeekArcChangeListener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch()
    {
        if (mOnSeekArcChangeListener != null)
        {
            mOnSeekArcChangeListener.onStopTrackingTouch(this);
        }
    }

    private void updateOnTouch(MotionEvent event)
    {
        boolean ignoreTouch = ignoreTouch(event.getX(), event.getY());
        if (ignoreTouch)
        {
            return;
        }
        setPressed(true);
        mTouchAngle = getTouchDegrees(event.getX(), event.getY());
        int progress = getProgressForAngle(mTouchAngle);
        progress = progress / mStep * mStep;
        onProgressRefresh(progress, true);
    }

    private boolean ignoreTouch(float xPos, float yPos)
    {
        boolean ignore = false;
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;

        float touchRadius = (float) Math.sqrt(((x * x) + (y * y)));
        if (touchRadius < mTouchIgnoreRadius)
        {
            ignore = true;
        }
        return ignore;
    }

    private double getTouchDegrees(float xPos, float yPos)
    {
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;
        //invert the x-coord if we are rotating anti-clockwise
        x = (mClockwise) ? x : -x;
        // convert to arc Angle
        double angle = Math.toDegrees(Math.atan2(y, x) + Math.PI / 2 - Math.toRadians(mRotation));
        if(isdebug)
        Log.i(TAG, "=================ignoreTouch y" + y + "=====angle:" + angle);
        if (angle < 0)
        {
            angle = 360 + angle;
        }
//		angle = (int) (mStartAngle + i*angle_seg + mRotation + 90);
        angle -= mStartAngle;
        if (angle < 0)
        {
            angle = 360 + angle;
        }
        if(isdebug)
        Log.i(TAG, "=================ignoreTouch x" + x + "=====angle:" + angle);
        return angle;
    }

    private int getProgressForAngle(double angle)
    {
        int touchProgress = (int) Math.round(valuePerDegree() * angle);

        touchProgress = (touchProgress < 0) ? INVALID_PROGRESS_VALUE
                : touchProgress;
        touchProgress = (touchProgress > mMax) ? INVALID_PROGRESS_VALUE
                : touchProgress;
        return touchProgress;
    }

    private float valuePerDegree()
    {
        return (float) (mMax - mMin) / mSweepAngle;
    }

    private void onProgressRefresh(int progress, boolean fromUser)
    {
        updateProgress(progress, fromUser);
    }

    private void updateThumbPosition()
    {
        int thumbAngle = (int) (mStartAngle + mProgressSweep + mRotation + 90);
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }

    private void updateProgress(int progress, boolean fromUser)
    {
        if(isdebug)
        Log.i(TAG, "========updateProgress " + progress);
        if (progress == INVALID_PROGRESS_VALUE)
        {
            return;
        }


        progress = (progress > mMax - mMin) ? mMax - mMin : progress;
        progress = (progress < 0) ? 0 : progress;
        if (mOnSeekArcChangeListener != null)
        {
            mOnSeekArcChangeListener.onProgressChanged(this, progress + mMin, fromUser);
        }
        mProgress = progress;

        mProgressSweep = (float) progress * mSweepAngle / (mMax - mMin);
        if(isdebug)
        Log.i(TAG, "=============onDraw::: " + mProgress + "======" + mProgressSweep);
        updateThumbPosition();

        invalidate();
    }

    /**
     * Sets a listener to receive notifications of changes to the SeekArc's
     * progress level. Also provides notifications of when the user starts and
     * stops a touch gesture within the SeekArc.
     *
     * @param l The seek bar notification listener
     * @see com.zd.myd.custome_view.SeekArc.OnSeekArcChangeListener
     */
    public void setOnSeekArcChangeListener(OnSeekArcChangeListener l)
    {
        mOnSeekArcChangeListener = l;
    }

    public int getmProgress()
    {
        return mProgress;
    }

    public void setProgress(int progress)
    {
        updateProgress(progress, false);
    }

    public int getProgressWidth()
    {
        return mProgressWidth;
    }

    public void setProgressWidth(int mProgressWidth)
    {
        this.mProgressWidth = mProgressWidth;
        mProgressPaint.setStrokeWidth(mProgressWidth);
    }

    public int getArcWidth()
    {
        return mArcWidth;
    }

    public void setArcWidth(int mArcWidth)
    {
        this.mArcWidth = mArcWidth;
        mArcPaint.setStrokeWidth(mArcWidth);
    }

    public int getArcRotation()
    {
        return mRotation;
    }

    public void setArcRotation(int mRotation)
    {
        this.mRotation = mRotation;
        updateThumbPosition();
    }

    public int getStartAngle()
    {
        return mStartAngle;
    }

    public void setStartAngle(int mStartAngle)
    {
        this.mStartAngle = mStartAngle;
        updateThumbPosition();
    }

    public int getSweepAngle()
    {
        return mSweepAngle;
    }

    public void setSweepAngle(int mSweepAngle)
    {
        this.mSweepAngle = mSweepAngle;
        updateThumbPosition();
    }

    public void setRoundedEdges(boolean isEnabled)
    {
        mRoundedEdges = isEnabled;
        if (mRoundedEdges)
        {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        } else
        {
            mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
            mProgressPaint.setStrokeCap(Paint.Cap.SQUARE);
        }
    }

    public void setTouchInSide(boolean isEnabled)
    {
        int thumbHalfheight = mThumb.getIntrinsicHeight() / 2;
        int thumbHalfWidth = mThumb.getIntrinsicWidth() / 2;
        mTouchInside = isEnabled;
        if (mTouchInside)
        {
            mTouchIgnoreRadius = (float) mArcRadius / 4;
        } else
        {
            // Don't use the exact radius makes interaction too tricky
            mTouchIgnoreRadius = mArcRadius
                    - Math.min(thumbHalfWidth, thumbHalfheight);
        }
    }

    public void setClockwise(boolean isClockwise)
    {
        mClockwise = isClockwise;
    }

    public int getmMax()
    {
        return mMax;
    }

    public int getmMin()
    {
        return mMin;
    }

    public void setmMax(int mMax)
    {
        this.mMax = mMax;
    }

    public void setmMin(int mMin)
    {
        this.mMin = mMin;
    }
}
//                if (getParent().getParent() != null && (mStartX != 0 || mStartY != 0))
//                {
//                    mMoveX = Math.abs(event.getRawX() - mStartX);
//                    mMoveY = Math.abs(event.getRawY() - mStartY);
//                    if(isdebug)
//                        Log.i(TAG, "========mMoveX " + mMoveX + "========mMoveY " + mMoveY +"====="+event.getAction());
//                    if (mMoveX <= mMoveY && (mMoveX!=0 && mMoveY/mMoveX >6)|| (mMoveX==0 && mMoveY > 0) || event.getAction() == MotionEvent.ACTION_DOWN)
//                    {
//                        canMove = false;
//                        getParent().getParent().requestDisallowInterceptTouchEvent(false);
//                    } else
//                    {
//                        canMove = true;
//                        getParent().getParent().requestDisallowInterceptTouchEvent(true);
//                    }
//                }