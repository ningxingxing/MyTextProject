package com.example.mytestproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class TextProgressView extends View {
    private Bitmap mBitmap;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float mMoveX;
    private Paint mProgressPaint;
    private int mMax = 100;
    private int mProgress = 0;
    private Paint mBgPaint;
    private int mBgColor;
    private int mProgressColor;
    private int mSeekBarSize = 10;
    private int mPaddingSize = 10;

    public TextProgressView(Context context) {
        this(context, null);
    }

    public TextProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

      //  TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BeautySeekBarView, defStyleAttr, 0);

     //   a.recycle();
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_x);
        mBitmapHeight = mBitmap.getHeight();

        mBgColor = context.getResources().getColor(R.color.editor_fragment_bg_color);
        mProgressColor = Color.BLUE;

        mProgressPaint = new Paint();
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setStrokeJoin(Paint.Join.ROUND);
        mProgressPaint.setStrokeWidth(mSeekBarSize);
        mProgressPaint.setAntiAlias(true);

        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mBgPaint.setStrokeJoin(Paint.Join.ROUND);
        mBgPaint.setStrokeWidth(mSeekBarSize);
        mBgPaint.setAntiAlias(true);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
        mMoveX = w / 2;
    }

    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float height = getHeight() / 2;
        canvas.drawLine(0 + getPaddingLeft(), height, getWidth() - getPaddingRight(), height, mBgPaint); //绘制直线
        canvas.drawLine(getWidth() / 2, height, mMoveX, height, mProgressPaint);
        canvas.drawBitmap(mBitmap,
                mMoveX - mBitmapWidth / 2 - getPaddingLeft(),
                height - mBitmapHeight / 2, null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        try {
            getParent().requestDisallowInterceptTouchEvent(true);
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:

                    break;
                case MotionEvent.ACTION_CANCEL:

                    break;
            }
        } catch (Exception ex) {

        }

        return super.dispatchTouchEvent(e);
    }


    /**
     * 判断MotionEvent事件是否位于thumb上
     *
     * @param event
     * @param thumbBounds
     * @return
     */
    private boolean isTouchInThumb(MotionEvent event, Rect thumbBounds) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (x >= thumbBounds.left && x <= thumbBounds.right && y >= thumbBounds.top && y <= thumbBounds.bottom)
            return true;
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                mMoveX = event.getX();
                calculationProgress();
                break;
        }
        invalidate();
        return true;
    }

    private void calculationProgress() {
        int width = getWidth() / 2;
        if (mMoveX > getWidth() - getPaddingLeft()) {
            mMoveX = getWidth() - getPaddingLeft();
        }

        if (mMoveX < getPaddingLeft()) {
            mMoveX = getPaddingLeft();
        }

        if (mMoveX >= width) {
            mProgress = (int) (mMax * (mMoveX - width + getPaddingLeft()) / width);
        } else {
            mProgress = (int) (mMax * (mMoveX - width - getPaddingLeft()) / width);
        }
        if (mProgress > mMax) {
            mProgress = mMax;
        }
        if (mProgress < -mMax) {
            mProgress = -mMax;
        }
    }


    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        this.mMax = max;
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        int width = getWidth() / 2;
        if (mProgress >= 0) {
            mMoveX = (progress * width / mMax) + width - getPaddingLeft();
        } else {
            mMoveX = (progress * width / mMax) + width + getPaddingLeft();
        }

        if (mMoveX > getWidth() - getPaddingLeft()) {
            mMoveX = getWidth() - getPaddingLeft();
        }

        if (mMoveX < getPaddingLeft()) {
            mMoveX = getPaddingLeft();
        }
        invalidate();
    }

    public int getBgColor() {
        return mBgColor;
    }

    public void setBgColor(int bgColor) {
        this.mBgColor = bgColor;
        mBgPaint.setColor(mBgColor);
        invalidate();
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        this.mProgressColor = progressColor;
        mProgressPaint.setColor(progressColor);
        invalidate();
    }

    public int getSeekBarSize() {
        return mSeekBarSize;
    }

    public void setSeekBarSize(int seekBarSize) {
        this.mSeekBarSize = seekBarSize;
    }

}