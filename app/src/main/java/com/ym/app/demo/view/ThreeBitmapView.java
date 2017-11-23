package com.ym.app.demo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.ym.app.demo.R;

/**
 * Created by wengyiming on 2017/11/22.
 */

public class ThreeBitmapView extends View {

    private Bitmap[] mBitmaps;
    Paint mPaint;
    Context mContext;
    int BlueColor;
    int PinkColor;
    int mWith;
    int mHeight;

    public ThreeBitmapView(Context context) {
        super(context);
        init(context);
    }

    public ThreeBitmapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ThreeBitmapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWith = getMeasuredWidth();
    }

    private void init(Context context) {
        mContext = context;
        BlueColor = ContextCompat.getColor(mContext, R.color.colorPrimary);
        PinkColor = ContextCompat.getColor(mContext, R.color.colorAccent);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mBitmaps = new Bitmap[]{BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setFilterBitmap(false);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(20);
        int sc = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            sc = canvas.saveLayer(0, 0, mWith, mHeight, null);
        }else {
            sc = canvas.saveLayer(0, 0, mWith, mHeight, null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG |
                    Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                    Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                    Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        }

        canvas.drawText(PorterDuff.Mode.DARKEN.name(), 200, 0, mPaint);
        canvas.drawBitmap(mBitmaps[1], 0, mBitmaps[1].getHeight() / 2, mPaint);
        canvas.drawBitmap(mBitmaps[2], mBitmaps[1].getWidth(), mBitmaps[1].getHeight() / 2, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
        canvas.drawBitmap(mBitmaps[0], mBitmaps[1].getWidth() / 2, mBitmaps[1].getHeight() / 2, mPaint);

        mPaint.setXfermode(null);
        // 还原画布
        canvas.restoreToCount(sc);
    }
}
