package com.example.mytestproject;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class DrawView extends View {
    private int scrHeight;
    private int scrWidth;
    public float currentX = 40;
    public float currentY = 50;
    private float mDownX;
    private float mDownY;
    private Paint pin;
    private Canvas mCanvas;
    //当前正在画的线
    private Line current = new Line();
    //所有画过的线
    private ArrayList<Line> lines = new ArrayList<Line>();
    private Bitmap mBitmap;
    private float mLastMoveX = 0;
    private float mLastMoveY = 0;
    private int mSpace;
    private boolean isUp = false;
    private Drawable mDrawable;
    private Matrix mMatrix;
    private int paintSize = 15;
    private final float KEY_PAINT_WIDTH = 2.5f;
    private Path path;
    float preX;
    float preY;
    private VelocityTracker mVelocityTracker;
    private float mSpeedX, mSpeedY;
    private float mPressure;
    private Path mLinePath = new Path();
    private Paint mPaint;

    private Paint mLinePaint;
    private final  int BREAK_LINE = 1;
    private final  int LINE = 2;
    private int mShape = LINE;

    private ShapeDrawable drawable;
    private final int RADIUS = 100; // 放大镜的半径
    private final int FACTOR = 2; // 放大倍数
    private Matrix matrix = new Matrix();
    private Bitmap bitmap_magnifier; // 放大镜位图
    private int m_left = 0; // 放大镜的左边距
    private int m_top = 0; // 放大镜的顶边距
    private Drawable mDrawableMag;


    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        //獲取屏幕的高
        scrHeight = dm.heightPixels;
        //獲取屏幕的寬
        scrWidth = dm.widthPixels;
        BlurMaskFilter PaintBGBlur = new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ALPHA_8;
        mDrawable = ContextCompat.getDrawable(context, R.mipmap.meinv);
        // mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.yezi,opt);
        //  mBitmap.extractAlpha();
        // mBitmap = getBitmapFromDrawable(context,R.drawable.ic_share_whatsapp);
        //mSpace = mDrawable.getIntrinsicWidth()/5;
        mSpace = mDrawable.getIntrinsicWidth() / 2;
        pin = new Paint(Paint.ANTI_ALIAS_FLAG);
        pin.setStyle(Paint.Style.STROKE);
        pin.setStrokeCap(Paint.Cap.ROUND);
        pin.setStrokeWidth(10);
        // pin.setMaskFilter(PaintBGBlur);
        //  pin.setMaskFilter(new EmbossMaskFilter(new float[]{20, 20, 20}, 0.5f, 2, 10));
        pin.setAntiAlias(true);
        pin.setColor(Color.WHITE);
        //  pin.setPathEffect(new DashPathEffect(new float[] {15, 5}, 0));

        //        mPaint.setStrokeWidth(mBitmap.getWidth());
//        mPaint.setFilterBitmap(true);
        // mPaint.setShader(new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.MIRROR));

//        BlurMaskFilter maskFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL);
//        pin.setMaskFilter(maskFilter);

        // pin.setStrokeWidth(mDrawable.getIntrinsicWidth());
        //Shader shader0 = new BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
//        Shader shader = new LinearGradient(0, 0, 30, 0, new int[]{Color.WHITE, Color.RED, Color.RED}, new float[]{0f, 0.5f, 1f}, Shader.TileMode.REPEAT);
//        Shader shader1 = new RadialGradient(10, 10, 10, new int[]{Color.GREEN, Color.WHITE, Color.GREEN}, new float[]{0, 0.5f, 1}, Shader.TileMode.MIRROR);
//        Shader shader2 = new SweepGradient(15, 15, Color.RED, Color.BLUE);
//        Shader shader3 = new ComposeShader(shader, shader1, PorterDuff.Mode.MULTIPLY);

//        LinearGradient mLinearGradient = new LinearGradient(0,0,getWidth(),0,new int[]{Color.BLUE,Color.WHITE,Color.BLUE}, null, Shader.TileMode.CLAMP);
//        pin.setShader(mLinearGradient);
//        mMatrix = new Matrix();
//        mLinearGradient.setLocalMatrix(mMatrix);
        //  pin.setShader(shader);

        path = new Path();
        mVelocityTracker = VelocityTracker.obtain();

        mPaint = new Paint();
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
      //  mPaint.setPathEffect(new DashPathEffect(new float[]{15, 5}, 0));

        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(10);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(context.getResources().getColor(R.color.pink));
        BlurMaskFilter maskFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.OUTER);
        mLinePaint.setMaskFilter(maskFilter);

//        bitmap_magnifier = BitmapFactory.decodeResource(getResources(),
//                R.mipmap.xing); //获取放大镜图像
     //   setBackgroundResource(R.mipmap.meinv);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e("nsc","onMeasure ="+getWidth() + " ="+getHeight());
        Bitmap bitmap_source = BitmapFactory.decodeResource(getResources(),
                R.mipmap.meinv); //获取要显示的源图像
        mBitmap = bitmap_source;
        BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(
                bitmap_source,
                getWidth() * FACTOR,
                getHeight() * FACTOR, true), Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP); //创建BitmapShader对象
// 圆形的drawable
        drawable = new ShapeDrawable(new RectShape());
        drawable.getPaint().setShader(shader);
        drawable.setBounds(0, 0, RADIUS * 2, RADIUS * 2); // 设置圆的外切矩形
    }

    public int getShape() {
        return mShape;
    }

    public void setShape(int shape) {
        this.mShape = shape;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mCanvas = canvas;

//        canvas.drawLine(mDownX, mDownY, currentX, currentY, pin);
//
//        for (int i = 0; i < lines.size(); i++) {
//            drawLine(canvas, lines.get(i));
//        }
//
//         drawLine(canvas, current);

//        if (mShape== LINE) {
//            canvas.save();
//            canvas.drawPath(path, mLinePaint);
//            canvas.drawPath(path, pin);
//            canvas.restore();
//        }else {
//            canvas.drawPath(mLinePath, mPaint);
//        }


        //pin.setPathEffect(new PathDashPathEffect(mLinePath, 15, 0, PathDashPathEffect.Style.TRANSLATE));
        // canvas.drawLine(0,0,500,500,mPaint);

     //   canvas.drawBitmap(mBitmap, 0, 0, null); // 绘制背景图像
     //   canvas.drawBitmap(bitmap_magnifier, 0, 0, null); // 绘制放大镜

       // drawable.draw(canvas); // 绘制放大后的图像
        canvas.drawRoundRect(0,0,mDrawable.getIntrinsicWidth()/2,mDrawable.getIntrinsicHeight()/2,30,30,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mBitmap,0,0,mPaint);
        mPaint.setXfermode(null);


    }

    private void drawLine(Canvas canvas, Line line) {
        for (int i = 0; i < line.points.size() - 1; i++) {
            float x = line.points.get(i).x;
            float y = line.points.get(i).y;

            float nextX = line.points.get(i + 1).x;
            float nextY = line.points.get(i + 1).y;
            canvas.drawText("★", x, y, pin);
            //   canvas.drawBitmap(mBitmap, nextX, nextY, pin);
            // canvas.drawLine(x, y, nextX, nextY, pin);
//            mDrawable.setBounds((int) nextX, (int) nextY, (int) nextX + mDrawable.getIntrinsicWidth(), (int) nextY + mDrawable.getIntrinsicHeight());
//            mDrawable.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);

                mDownX = event.getX();
                mDownY = event.getY();
                mLastMoveX = mDownX;
                mLastMoveY = mDownY;
                if (mShape== LINE) {
                    path.moveTo(mDownX, mDownY);
                }else {
                    mLinePath.moveTo(mDownX, mDownY);
                }

                preX = mDownX;
                preY = mDownY;
                break;

            case MotionEvent.ACTION_MOVE:
                mPressure = event.getPressure();
                // pin.setStrokeWidth(mPressure*100);
                Log.e("nsc", "pressure =" + mPressure);
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                mSpeedX = mVelocityTracker.getXVelocity();
                mSpeedY = mVelocityTracker.getYVelocity();

                currentX = event.getX();
                currentY = event.getY();
                if (mShape== LINE) {
                    path.quadTo(preX, preY, (currentX + preX) / 2, (currentY + preY) / 2);
                }else {
                    mLinePath.lineTo(currentX, currentY);
                }

                preX = currentX;
                preY = currentY;
                if (Math.abs(currentX - mLastMoveX) > mSpace || Math.abs(currentY - mLastMoveY) > mSpace) {
                    ViewPoint point = new ViewPoint();
                    point.x = currentX;
                    point.y = currentY;
                    current.points.add(point);

                    mLastMoveX = currentX;
                    mLastMoveY = currentY;
                }

                final int x = (int) event.getX(); // 获取当前触摸点的X轴坐标
                final int y = (int) event.getY(); // 获取当前触摸点的Y轴坐标
                matrix.setTranslate(RADIUS - x * FACTOR, RADIUS - y * FACTOR); // 平移到绘制shader的起始位置
                drawable.getPaint().getShader().setLocalMatrix(matrix);
             //   drawable.setBounds(x - RADIUS, y - RADIUS, x + RADIUS, y + RADIUS); // 设置圆的外切矩形

                break;

            case MotionEvent.ACTION_UP:
                //mLinePath.lineTo(event.getX(),event.getY());
                //mLinePath.lineTo(event.getX(), event.getY());
                isUp = true;
                current.setPath(mLinePath);
                lines.add(current);
                current = new Line();

                // path.reset();
                break;

        }
        invalidate();
        return true;
    }

    private float controlPaint(double v) {
        //余弦函数
        //y=0.5*[cos(x*PI)+1]
        float result = KEY_PAINT_WIDTH * paintSize;
        if (v < 0) {

        } else if (v < 1) {
            result = (float) (0.5 * paintSize * KEY_PAINT_WIDTH * (Math.cos(v * Math.PI) + 1));
        } else {
            result = ((float) (paintSize / v > 0.1 ? paintSize / v : 0.1));
        }
        return result;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVelocityTracker.recycle();
    }
}
