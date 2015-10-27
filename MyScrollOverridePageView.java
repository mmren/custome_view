package com.zd.myd.custome_view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zd.myd.tools.LogUtil;
import com.zd.myd.tools.ScreenUtil;
import com.zd.myd.tools.StringUtils;
import com.zd.myd.ui.mine.repay.PageTag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
/**
 * Created by renmingming on 15/10/24.
 */
public class MyScrollOverridePageView extends ViewGroup
{
    private static final String LOG_TAG = "MyScrollPageView";

    private int mPages;

    private PageTag mCurrentPage;

    public boolean isScrolling;

    private int screenWidth;

    private int scrollDisAll;

    private boolean moverientation;//true left  false right

    int start_x = 0;

    int start_y = 0;

    int mCurrent_x = 0;

    public static final String MROOTTAG = "root";


    public View mCurrentView;

    private final ArrayList<PageTag> tags = new ArrayList<PageTag>();


    private LinkedHashMap<String, View> views = new LinkedHashMap<String, View>();

    public MyScrollOverridePageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        screenWidth = ScreenUtil.getScreenWidth(context);
    }


    private void addPageTag(String tag)
    {
        if (mCurrentPage != null)
        {
            PageTag pt = new PageTag();
            pt.tag = tag;
            pt.mprePage = mCurrentPage;
            mCurrentPage.mNextPage = pt;
            mCurrentPage = pt;

        } else
        {
            mCurrentPage = new PageTag();
            mCurrentPage.tag = tag;
        }
        tags.add(mCurrentPage);
    }


    public void addView(View child, LayoutParams params, String currentTage, String tage)
    {
        if (!StringUtils.isEmpty(tage) && tags.contains(tage))
        {
//            mCurrentPage = tags.indexOf(tage)+1;
        }
        if (!views.containsKey(tage))
        {

            mPages++;
            addPageTag(tage);


            views.put(tage, child);


            addView(child, params);
        } else
        {
            setmCurrentPage(tage);
        }
    }

    public void addView(View child, LayoutParams params, String tage)
    {
        addView(child, params, null, tage);
    }

    public void addView(View child, String ctage, String tage)
    {
        addView(child, new LayoutParams(screenWidth, getMeasuredHeight()), ctage, tage);
    }

    public void addView(View child, String TYPE)
    {
        addView(child, new LayoutParams(screenWidth, getMeasuredHeight()), TYPE);
    }

    @Override
    public void addView(View child)
    {
        addView(child, new LayoutParams(screenWidth, getMeasuredHeight()));

    }

    @Override
    public void addView(View child, LayoutParams params)
    {
        if (!views.containsKey(MROOTTAG))
        {
            mPages++;
            views.put(MROOTTAG, child);
            addPageTag(MROOTTAG);
            super.addView(child, params);
            getmCurrentView();
        } else if (views.size() < getChildCount())
        {
            throw new AddViewException();
        } else
        {
            super.addView(child, params);
            moverientation = true;
            getmCurrentView();
            smallAddView();
        }


    }

    public void smallAddView()
    {
        smallScroll(screenWidth, 250);
    }

    public void Next()
    {
        if(mCurrentPage!= null && mCurrentPage.mNextPage !=null){
            mCurrentPage = mCurrentPage.mNextPage;
        }
        dosmall(true);
    }

    public void prev()
    {
        if(mCurrentPage!= null && mCurrentPage.mprePage !=null){
            mCurrentPage = mCurrentPage.mprePage;
        }
        dosmall(false);
    }

    public void setmCurrentPage(String tag)
    {
        for(PageTag pageTag : tags){
            if(!StringUtils.isEmpty(tag) && tag.equals(pageTag.tag)){
                setmCurrentPage(pageTag);
                break;
            }
        }
    }

    public void setmCurrentPage(PageTag currentPage)
    {
        mCurrentPage.mNextPage = currentPage;
        currentPage.mprePage = mCurrentPage;

        mCurrentPage = currentPage;

        getmCurrentView();
        dosmall(true);
    }

    private void dosmall(boolean preOrNext)
    {
        moverientation = preOrNext;
        int dis = 0;
        if (moverientation)
        {
            dis = screenWidth;
        } else
        {
            dis = -screenWidth;
        }
        smallScroll(dis, 250);

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 计算所有ChildView的宽度和高度 然后根据ChildView的计算结果，设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);


        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        /**
         * 记录如果是wrap_content是设置的宽和高
         */
        int width = 0;
        int height = 0;

        int cCount = getChildCount();

        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;


        /**
         * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
         */
        for (int i = 0; i < cCount; i++)
        {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            LayoutParams lp = childView.getLayoutParams();
            if (lp instanceof MarginLayoutParams)
            {
                cParams = (MarginLayoutParams) childView.getLayoutParams();
                cWidth = cWidth + cParams.leftMargin + cParams.rightMargin;
                cHeight = cHeight + cParams.topMargin + cParams.bottomMargin;
            }
            height = height > cHeight ? height : cHeight;
            width = width > cWidth ? width : cWidth;
        }
        /**
         * 如果是wrap_content设置为我们计算的值
         * 否则：直接设置为父容器计算的值
         */
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        int i = 0;
        for (String key : views.keySet())
        {
            View child = views.get(key);
            if (child.getVisibility() != GONE)
            {
                int rootH = b - t;
                LayoutParams st = child.getLayoutParams();
                st.width = screenWidth;
                st.height = rootH;
                child.setLayoutParams(st);
                int left = 0;
                int right = screenWidth;

                child.layout(left, 0, right, rootH);
                i++;
            }
        }

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        LogUtil.i(LOG_TAG, "onTouch : " + event.getAction() + ": " + event.getX());
        return super.onInterceptTouchEvent(event);
    }


    private void getmCurrentView()
    {
        mCurrentView = views.get(mCurrentPage.tag);
        mCurrentView.bringToFront();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        if (isScrolling)
        {
            return false;
        }
        boolean isdispath = super.dispatchTouchEvent(event);
        return true;
    }
    public void smallScroll(final int distance, final int duration)
    {
        if (distance == 0 || duration == 0)
        {
            return;
        }
        isScrolling = true;

        final int MaxDistance = moverientation ? 0 : screenWidth;

        if (moverientation)
        {
            mCurrentView.scrollTo(-screenWidth, 0);
        } else
        {
            mCurrentView.scrollTo(0, 0);
        }

        int step = (int) (distance / duration);

        if (Math.abs(step) > 0)
        {
            final int lastdistance = Math.abs(distance % duration);
            final int finalStep = step;
            for (int i = 0; i < duration; i++)
            {
                final int i_f = i;
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int c_step = finalStep > 0 ? finalStep + 1 : finalStep - 1;
                        mCurrentView.scrollBy(lastdistance - i_f > 0 ? c_step : finalStep, 0);

                        if (i_f == duration - 1)
                        {
                            mCurrentView.scrollTo(MaxDistance, 0);
                            isScrolling = false;
                            getmCurrentView();
                        }
                    }
                }, i + 10);

            }
        } else
        {
            int duration_step = Math.abs((int) (duration / distance));

            int lastduration = Math.abs(distance % duration);
            for (int i = 0; i < Math.abs(distance); i++)
            {
                final int i_f = i;
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mCurrentView.scrollBy(distance > 0 ? 1 : -1, 0);

                        if (i_f == Math.abs(distance) - 1)
                        {
                            mCurrentView.scrollTo(MaxDistance, 0);
                            isScrolling = false;
                            getmCurrentView();
                        }
                    }
                }, lastduration - i > 0 ? i * duration_step + 11 : i * duration_step + 10);

            }

        }

    }


    class AddViewException extends RuntimeException
    {
        @Override
        public String getMessage()
        {
            return "Can note add view with no Tag";
        }
    }

    class SetCurrentException extends RuntimeException
    {
        @Override
        public String getMessage()
        {
            return "current page is not invalid";
        }
    }
}
