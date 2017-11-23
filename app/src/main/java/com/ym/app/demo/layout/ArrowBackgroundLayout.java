package com.ym.app.demo.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ym.app.demo.R;

/**
 * Created by wengyiming on 2017/11/23.
 */

public class ArrowBackgroundLayout extends LinearLayout {
    Paint mPaint;
    Context mContext;
    int BlueColor;
    int PinkColor;
    int mWidth;
    int mHeight;
    RectF backRect;
    int cornersRadiusX = 10;
    int cornersRadiusY = 10;
    Path path;
    int mArrowSize = 50;
    int mArrowStartX = 0;

    float mArrowOffset = 0f;


    public void setArrowOffset(float arrowOffset) {
        mArrowOffset = arrowOffset;
        mArrowStartX = (int) (((mWidth - 2 * mArrowSize) - mArrowStartX) * (1 - mArrowOffset));
        mArrowStartX = Math.max(mArrowStartX, cornersRadiusX) + 5;
        invalidate();
    }


    public void setArrowOffsetMid() {
        mArrowStartX = ((mWidth - 2 * mArrowSize) - mArrowStartX) / 2;
    }

    public ArrowBackgroundLayout(Context context) {
        super(context);
        init(context);
    }

    public ArrowBackgroundLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArrowBackgroundLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }


    private void init(Context context) {
        mContext = context;
        BlueColor = ContextCompat.getColor(mContext, android.R.color.white);
        PinkColor = ContextCompat.getColor(mContext, R.color.colorAccent);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        path = new Path();
        mArrowStartX = Math.max(mArrowStartX, cornersRadiusX) + 5;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        mPaint.setColor(PinkColor);
        RectF rectF = new RectF(0, 0, mWidth, mArrowSize);
        canvas.drawRect(rectF, mPaint);


        float left = 0;
        float top = mArrowSize;
        float right = mWidth;
        float bottom = mHeight - mArrowSize;
        mPaint.setColor(BlueColor);
        backRect = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(backRect, cornersRadiusX, cornersRadiusY, mPaint);


        path.moveTo(left + mArrowStartX, top);
        path.lineTo(left + mArrowSize + mArrowStartX, top);
        path.lineTo(left + mArrowSize / 2 + mArrowStartX, top - mArrowSize);

        path.close();
        canvas.drawPath(path, mPaint);

        super.dispatchDraw(canvas);


    }

}
