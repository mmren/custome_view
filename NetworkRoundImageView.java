package com.zd.myd.custome_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.zd.myd.R;
/**
 * Created by renmingming on 15/9/21.
 */
public class NetworkRoundImageView extends MyNetworkImageView
{

    private Paint mPaint;



    public NetworkRoundImageView(Context context) {
        super(context);
        initVar();
    }


    public NetworkRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVar();
    }


    public NetworkRoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVar();
    }


    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();


        if (drawable == null) {
            return;
        }


        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap b = null;
        try {
            b = ((BitmapDrawable) drawable).getBitmap();
        } catch (ClassCastException e) {
            return;
        }

        if (null==b) {
            b=BitmapFactory.decodeResource(getResources(), R.mipmap.head_portrait);
        }

        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);


        int w = getWidth(), h = getHeight();

        int validSize = Math.min(w,h);
        Bitmap roundBitmap = getCroppedBitmap(bitmap, validSize);
        canvas.drawBitmap(roundBitmap, 0, 0, null);


        mPaint.setColor(0xFFFFFFFF);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        int radius = Math.min(roundBitmap.getWidth()/2,
                roundBitmap.getHeight()/2);
        canvas.drawCircle(roundBitmap.getWidth()/2,
                roundBitmap.getHeight()/2,
                radius-2,
                mPaint);

    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, final int radius) {
//            Bitmap sbmp;
//            if (bmp.getWidth() != radius || bmp.getHeight() != radius)
//            {
//                sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
//            } else{
//                sbmp = bmp;
//            }
        Bitmap output = Bitmap.createBitmap(radius, radius, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

//            canvas.drawARGB(0, 0, 0, 0);
//            paint.setColor(Color.parseColor("#BAB399"));
//          canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f,
//          sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
        canvas.drawCircle(radius/2 , radius / 2 , radius/2 , paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

        Bitmap targetBitmap;
        if (bmp.getWidth()<bmp.getHeight()) {
            targetBitmap = Bitmap.createScaledBitmap(bmp, radius, bmp.getHeight()*radius/bmp.getWidth(), false);
        } else {
            targetBitmap = Bitmap.createScaledBitmap(bmp, bmp.getWidth()*radius/bmp.getHeight(), radius, false);
        }

        final Rect rect = new Rect(0, 0, radius, radius);
        canvas.drawBitmap(targetBitmap, rect, rect, paint);


        return output;
    }

    private void initVar() {
        mPaint = new Paint();
    }

}
