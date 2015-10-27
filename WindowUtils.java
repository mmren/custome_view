package com.zd.myd.custome_view;


import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zd.myd.R;
import com.zd.myd.tools.DensityUtil;
import com.zd.myd.tools.LogUtil;
import com.zd.myd.tools.ScreenUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

/**
 * 弹窗辅助类
 * Created by renmingming on 15/9/30.
 */
public class WindowUtils implements OnClickListener
{

    private static final String LOG_TAG = "WindowUtils";

    private View mView = null;

    private WindowManager mWindowManager = null;

    private Context mContext = null;

    public Boolean isShown = false;

    public View contentView;

    private View tran_view;

    private final HashMap<View, ClickCallback> callbacks = new HashMap<View, ClickCallback>();

    private final Stack<View> views = new Stack<View>();

    private final ArrayList<Integer> hids = new ArrayList<Integer>();

    private DestoryCallback destoryCallback;


    private boolean isComfirmDialog;


    public static synchronized WindowUtils getInstance(Context context)
    {

        return new WindowUtils(context);
    }

    private WindowUtils(Context context)
    {
        mContext = context;

    }


//    /**
//     * 显示弹出框
//     *
//     * @param ovrrideId                   非透明区域id
//     * @param isNeedhidByClickTransparent 是否需要点击透明区域消失
//     */
//    public View showPopupWindow(MyScrollPageView mMoveContent,String tag, int layoutId, int ovrrideId, boolean isNeedhidByClickTransparent, int animalStyle)
//    {
//        if (isShown)
//        {
//            LogUtil.i(LOG_TAG, "return cause already shown");
////            return null;
//        }
//        isShown = true;
//        LogUtil.i(LOG_TAG, "showPopupWindow");
//        // 获取WindowManager
//        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//
//
//        WindowManager.LayoutParams params = setupLayoutParams();
//        if (views.empty())
//        {
//            params.windowAnimations = R.style.dialog_bg_style;
//            tran_view = new View(mContext);
//            tran_view.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_bankcards));
//            mWindowManager.addView(tran_view, params);
//            views.push(tran_view);
//        } else if (mMoveContent != null)
//        {
//            mView = LayoutInflater.from(mContext).inflate(layoutId, null);
//            int w = tran_view.getWidth();
//            FrameLayout.LayoutParams rl = new FrameLayout.LayoutParams(w, DensityUtil.dip2px(mContext, 372));
////            rl.leftMargin = w;
//            mMoveContent.addView(mView, rl,tag);
////            int b = tran_view.getHeight();
////            int t = b - DensityUtil.dip2px(mContext, 372);
////            mView.postInvalidate();
//        } else
//        {
////            mLayoutId = layoutId;
//            mView = LayoutInflater.from(mContext).inflate(layoutId, null);
////            mView = setUpView(layoutId, ovrrideId, isNeedhidByClickTransparent);
//            params = setupLayoutParams();
//            if (animalStyle > 0)
//            {
//                params.windowAnimations = animalStyle;
//            }
//            params.windowAnimations = animalStyle;
//            mWindowManager.addView(mView, params);
//
//            LogUtil.i(LOG_TAG, "add view");
//            views.push(mView);
//        }
//        return mView;
//    }


    /**
     * 显示弹出框
     *
     * @param ovrrideId                   非透明区域id
     * @param isNeedhidByClickTransparent 是否需要点击透明区域消失
     */
    public View showPopupWindow(int layoutId, int ovrrideId, boolean isNeedhidByClickTransparent, int animalStyle)
    {
        showPopupWindow(layoutId, ovrrideId, isNeedhidByClickTransparent, animalStyle, setupLayoutParams());
        return mView;
    }

    /**
     * 显示弹出框
     *
     * @param ovrrideId                   非透明区域id
     * @param isNeedhidByClickTransparent 是否需要点击透明区域消失
     */
    public View showPopupWindow(int layoutId, int ovrrideId, boolean isNeedhidByClickTransparent, int animalStyle, WindowManager.LayoutParams params)
    {


        if (isShown)
        {
            LogUtil.i(LOG_TAG, "return cause already shown");
//            return null;
        }

        isComfirmDialog = layoutId == R.layout.dialog_cancel_comfrim;

        isShown = true;
        LogUtil.i(LOG_TAG, "showPopupWindow");

        // 获取WindowManager
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//        mLayoutId = layoutId;
        mView = setUpView(layoutId, ovrrideId, isNeedhidByClickTransparent);
        if (views.empty())
        {
            WindowManager.LayoutParams params_tran = setupLayoutParams();
            params_tran.windowAnimations = R.style.dialog_bg_style;
            tran_view = new View(mContext);
            tran_view.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_bankcards));
            mWindowManager.addView(tran_view, params_tran);
            views.push(tran_view);
        }
        if (animalStyle > 0)
        {
            params.windowAnimations = animalStyle;
        }
        mWindowManager.addView(mView, params);

        LogUtil.i(LOG_TAG, "add view");
        views.push(mView);
        return mView;
    }


    public static LayoutParams setupLayoutParams()
    {

        LayoutParams params = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 类型
        params.type = LayoutParams.TYPE_TOAST;

        // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

        // 设置flag
//         如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件

        int flags =
//                LayoutParams.FLAG_NOT_FOCUSABLE
//                |
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
        // 不设置这个flag的话，home页的划屏会有问题

        params.gravity = Gravity.BOTTOM;
        return params;
    }

    public void destory()
    {
        if (destoryCallback != null)
        {
            destoryCallback.destory();
        }
    }

    /**
     * 隐藏弹出框
     */
    public void hidePopupWindow(boolean isAll)
    {
        LogUtil.i(LOG_TAG, "hide " + isShown + ", " + mView);
        if (isAll)
        {
            for (View v : views)
            {
                LayoutParams lp = (LayoutParams) v.getLayoutParams();
                lp.windowAnimations = R.style.dialog_bg_style;
                v.setLayoutParams(lp);
                mWindowManager.removeView(v);
                isShown = false;

            }
        } else
        {
            if (isShown && null != mView)
            {
                LogUtil.i(LOG_TAG, "hidePopupWindow");
                mWindowManager.removeView(views.peek());
                views.pop();
                if (!views.isEmpty())
                {
                    mView = views.peek();
                    if (mView.equals(tran_view))
                    {
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (views.size() == 1)
                                {
                                    mWindowManager.removeView(tran_view);
                                    views.pop();
                                    isShown = false;
                                }
                            }
                        }, 300);
                    }
                }
            }
        }
    }

    /**
     * 注册隐藏popwindow 事件
     *
     * @param hidId
     */
    public void registhideListener(int hidId)
    {
        hids.add(hidId);
        View v = mView.findViewById(hidId);
        v.setOnClickListener(this);
//        View hidview = mView.findViewById(hidId);
//        hidview.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
////                LogUtil.i(LOG_TAG, "ok on click");
//                hidePopupWindow();
//
//            }
//        });
    }

    /**
     * @param context
     * @param LayoutId
     * @param ovrrideId                   非透明区域id
     * @param isNeedhidByClickTransparent 是否需要点击透明区域消失
     * @return
     */

    long time = 0;

    private View setUpView(int LayoutId, int ovrrideId, boolean isNeedhidByClickTransparent)
    {
        LogUtil.i(LOG_TAG, "setUp view");

        final View view = LayoutInflater.from(mContext).inflate(LayoutId, null);


        if (isNeedhidByClickTransparent)
        {
            // 点击窗口外部区域可消除
            // 这点的实现主要将悬浮窗设置为全屏大小，外层有个透明背景，中间一部分视为内容区域
            // 所以点击内容区域外部视为点击悬浮窗外部
            if (contentView == null)
                contentView = view.findViewById(ovrrideId);// 非透明的内容区域

            view.setOnTouchListener(new OnTouchListener()
                                    {

                                        @Override
                                        public boolean onTouch(View v, MotionEvent event)
                                        {
                                            int start_x = (int) event.getRawX();
                                            int start_y = (int) event.getRawY();
                                            Rect rect = new Rect();
                                            contentView.getGlobalVisibleRect(rect);
                                            if (!rect.contains(start_x, start_y))
                                            {
//                                                hidePopupWindow(true);

                                                return false;
                                            }

                                            return true;
                                        }
                                    }
            );
        }
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        // 点击back键可消除
        view.setOnKeyListener(new OnKeyListener()
                              {
                                  @Override
                                  public boolean onKey(final View v, int keyCode, KeyEvent event)
                                  {
                                      long now = new Date().getTime();
                                      if (time == 0 || now - time > 500)
                                      {
                                          time = now;
                                          switch (keyCode)
                                          {
                                              case KeyEvent.KEYCODE_BACK:
                                                  new Handler().postDelayed(new Runnable()
                                                  {
                                                      @Override
                                                      public void run()
                                                      {
//                                                          if (v == mView || mView==v.getParent())
//                                                          {
                                                          hidePopupWindow(false);
//                                                          }
                                                          if (!isComfirmDialog)
                                                          {
                                                              destory();
                                                          }
                                                      }
                                                  }, 100);


                                              default:
                                                  return false;
                                          }
                                      }
                                      return false;
                                  }

                              }
        );

        return view;

    }

    public void registDestoryCallback(DestoryCallback callback)
    {
        this.destoryCallback = callback;
    }

    public void registClickEvent(int vid, ClickCallback callback)
    {
        View v = mView.findViewById(vid);
        v.setOnClickListener(this);
        callbacks.put(v, callback);
    }

    @Override
    public void onClick(final View v)
    {
//        v.setClickable(false);
        ClickCallback cb = callbacks.get(v);
        if (cb != null)
        {
            cb.onClick(v);
        }
        if (hids.contains(v.getId()))
        {
            hidePopupWindow(false);
        }
//        new Handler().postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                v.setClickable(true);
//            }
//        }, 500);

    }


    public interface ClickCallback
    {
        void onClick(View v);
    }

    public interface DestoryCallback
    {
        void destory();
    }

    ;

    public View getmView()
    {
        return mView;
    }
}
