package com.zd.myd.custome_view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.zd.myd.R;
import com.zd.myd.tools.ToastUtils;

import java.io.File;
/**
 * Created by renmingming on 15/10/13.
 * <获取头像>
 * <功能详细描述>
 *
 * @see [类、类#方法、类#成员]
 */
public class ShowPhotoDiaLog

{
    /**
     * 拍照
     */
    public static final int NUM1 = 1;

    /**
     * 相册
     */
    public static final int NUM2 = 2;

    public static String path;


    public static void showPhotoDiaLog(final String logoPath, final Activity mContext)
    {

        path = logoPath;


        final WindowUtils windowUtils = WindowUtils.getInstance(mContext);
        windowUtils.showPopupWindow(R.layout.image_select_dialog, R.id.content_ll, true, R.style.anim_menu_bottombar);
        View rootView = windowUtils.getmView();


        Button camera = (Button) rootView.findViewById(R.id.camera);
        Button gallery = (Button) rootView.findViewById(R.id.gallery);
        Button cancel = (Button) rootView.findViewById(R.id.cancel);

        int cancelId = cancel.getId();
        int galleryId = gallery.getId();
        int cameraId = camera.getId();


        windowUtils.registhideListener(cancelId);
        windowUtils.registhideListener(galleryId);
        windowUtils.registhideListener(cameraId);
        /**
         * 从相册选取照片
         */
        windowUtils.registClickEvent(cameraId, new WindowUtils.ClickCallback()
        {
            @Override
            public void onClick(View v)
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                                {
                                    try
                                    {
                                        File picture = new File(logoPath);
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        Uri imageFileUri = Uri.fromFile(picture);
                                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
                                        mContext.startActivityForResult(intent, NUM1);
                                    } catch (ActivityNotFoundException e)
                                    {
                                        ToastUtils.showShortText(mContext, "没有找到储存目录");
                                    }
                                } else
                                {
                                    ToastUtils.showShortText(mContext, "没有储存卡");
                                }
                            }
                        }, 500);
                    }
                }, 500);
            }
        });
        /**
         * 从相机拍摄照片
         */
        windowUtils.registClickEvent(galleryId, new WindowUtils.ClickCallback()
        {
            @Override
            public void onClick(View v)
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        mContext.startActivityForResult(intent, NUM2);
                    }
                }, 500);
            }
        });




    }
}
