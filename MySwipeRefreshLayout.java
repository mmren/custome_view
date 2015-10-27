package com.zd.myd.custome_view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * Created by renmingming on 15/10/11.
 */
public class MySwipeRefreshLayout extends SwipeRefreshLayout
{

    private OnRefreshingListener my_Listener;

    public MySwipeRefreshLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshingListener
    {
        public void onRefreshing();
    }

    public OnRefreshingListener getMy_Listener()
    {
        return my_Listener;
    }

    public void setMy_Listener(OnRefreshingListener my_Listener)
    {
        this.my_Listener = my_Listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)
        {
            my_Listener.onRefreshing();
        }

        return super.onTouchEvent(event);
    }
}
