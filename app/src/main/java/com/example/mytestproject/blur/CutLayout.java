package com.example.mytestproject.blur;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;

/**
 * Created by jrvansuita on 19/02/17.
 */

public class CutLayout extends RelativeLayout {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Xfermode pdMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private Path path = new Path();
    private float mMoveX = 0;
    private float mMoveY = 0;
    private Paint mCirclePaint = new Paint();
    private boolean isShow = false;
    private float mOldDistance = 1.0f;
    private float mDownX = 0;
    private float mDownY = 0;
    private float mScale = 1.0f;
    private int mTouchSlop;
    private float mLastScale = 1f;
    private boolean isScale = false;
    private Paint mBlurPaint;
    private int mCircleSize = 50;

    private ICutLayoutListener iCutLayoutListener;

    public interface ICutLayoutListener {
        void onMove(float x, float y);

    }

    public void setCutLayoutListener(ICutLayoutListener listener) {
        this.iCutLayoutListener = listener;
    }

    public CutLayout(Context context) {
        this(context, null);
    }

    public CutLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CutLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(STROKE);
        mCirclePaint.setColor(Color.WHITE);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        BlurMaskFilter maskFilter = new BlurMaskFilter(mCircleSize / 2, BlurMaskFilter.Blur.NORMAL);
        mBlurPaint = new Paint();
        mBlurPaint.setMaskFilter(maskFilter);
        mBlurPaint.setStyle(STROKE);
        mBlurPaint.setStrokeWidth(mCircleSize);
        mBlurPaint.setColor(Color.WHITE);
        mBlurPaint.setAlpha(20);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        if (!isScale) {
            paint.setXfermode(pdMode);
            path.reset();
            path.addCircle(mMoveX, mMoveY, mScale * getWidth() / 4, Path.Direction.CW);
            Log.e("nsc", "dispatchDraw mMoveX=" + mMoveX + " mMoveY=" + mMoveY);
            canvas.drawPath(path, paint);
            canvas.restoreToCount(saveCount);
            paint.setXfermode(null);
        }
        if (isShow) {
            canvas.drawCircle(mMoveX, mMoveY, mScale * getWidth() / 4 - mCircleSize / 2, mCirclePaint);
        }
        canvas.drawCircle(mMoveX, mMoveY, mScale * getWidth() / 4 - mCircleSize / 2, mBlurPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("nsc", "dispatchDraw mMoveX=" + mMoveX + " mMoveY=" + mMoveY + " event=" + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                mMoveX = event.getX();
                mMoveY = event.getY();
//                if (iCutLayoutListener!=null){
//                    iCutLayoutListener.onMove(mMoveX,mMoveY);
//                }
                isShow = true;
                invalidate();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mOldDistance = distance(event);
                break;

            case MotionEvent.ACTION_MOVE:
                mMoveX = event.getX();
                mMoveY = event.getY();
                float diffX = event.getX() - mDownX;
                float diffY = event.getY() - mDownY;
                boolean isMove = Math.abs(diffX) > mTouchSlop || Math.abs(diffY) > mTouchSlop;
                if (event.getPointerCount() == 2 && isMove) {
                    isScale = true;
                    float newDist = distance(event);
                    float space = (newDist - mOldDistance);
                    float scale = getScaleX() + space / getWidth();
                    float diffScale = scale - mLastScale;
                    mScale = mScale + diffScale;
                    //  mScale = mScale + space / getWidth()/3;

                    mLastScale = scale;
                } else if (event.getPointerCount() == 1 && isMove) {
//                    if (iCutLayoutListener!=null){
//                        iCutLayoutListener.onMove(mMoveX,mMoveY);
//                    }
                    isScale = false;
                }
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                mMoveX = event.getX();
                mMoveY = event.getY();
                isShow = false;
                invalidate();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                float upX = event.getX() - mDownX;
                float upY = event.getY() - mDownY;

                if (Math.abs(upX) < 5 && Math.abs(upY) < 5) {
                    mScale = 1.0f;
                }
                break;
        }
        return true;
    }

    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

}