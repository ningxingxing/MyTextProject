package com.example.mytestproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;

import com.example.mytestproject.blur.CutLayout;

public class BlurActivity extends Activity {
    private ImageView ivRawImage;
    private AppCompatImageView ivBlurredImage;
    private CutLayout mCutLayout;
    private int radius = 10;
    private SeekBar mSeekBar;
    private Bitmap mBitmap;
    private Bitmap mOutputBitmap;
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur blurScript;
    private ScriptC_blur sketchScript;
    private float mProgress = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);

      //  ivRawImage = (ImageView) findViewById(R.id.raw_image);
        ivBlurredImage = (AppCompatImageView) findViewById(R.id.blurred_image);
      //  mCutLayout = (CutLayout) findViewById(R.id.cut_layout);
        // ivRawImage.setImageResource(R.mipmap.hua);
        mSeekBar = findViewById(R.id.seekBar);

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.meiguo);
//        ivBlurredImage.setImageBitmap(fastBlur(this, bitmap, 5));

//        GaussianBlur.with(BlurActivity.this)
//                .size(300)
//                .radius(1)
//                .setViewWidth(mCutLayout.getWidth())
//                .setViewHeight(mCutLayout.getHeight())
//                .put(R.mipmap.hua, ivBlurredImage, (int) 1, (int) 1, radius);

//        mCutLayout.setCutLayoutListener(new CutLayout.ICutLayoutListener() {
//            @Override
//            public void onMove(float x, float y) {
//                radius ++;
//                GaussianBlur.with(BlurActivity.this)
//                        .size(600)
//                        .radius(1)
//                        .setViewWidth(mCutLayout.getWidth())
//                        .setViewHeight(mCutLayout.getHeight())
//                        .put(R.mipmap.hua, ivBlurredImage,(int)x,(int)y,radius);
//            }
//        });

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.hua);
        mRenderScript = RenderScript.create(BlurActivity.this);
        sketchScript = new ScriptC_blur(mRenderScript);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mOutputBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);

                Allocation in = Allocation.createFromBitmap(mRenderScript,mBitmap);
                Allocation out = Allocation.createTyped(mRenderScript,in.getType());
                sketchScript.set_gWidth(mBitmap.getWidth());
                sketchScript.set_gHeight(mBitmap.getHeight());
                sketchScript.set_gIn(in);
                sketchScript.set_clickX(mBitmap.getWidth()/2);
                sketchScript.set_clickY(mBitmap.getHeight()/2);
                Log.e("nsc","onProgressChanged ="+progress*1f/10);
                mProgress = progress/10f;
                sketchScript.set_radius(mProgress);
                sketchScript.forEach_invert(in,out);

                out.copyTo(mOutputBitmap);
                ivBlurredImage.setImageBitmap(mOutputBitmap);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mRenderScript==null) {
                    mRenderScript = RenderScript.create(BlurActivity.this);
                }
                if (sketchScript==null) {
                    sketchScript = new ScriptC_blur(mRenderScript);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ivBlurredImage.setImageBitmap(mOutputBitmap);
                if (mRenderScript!=null){
                    mRenderScript.destroy();
                    mRenderScript = null;
                }
                if (sketchScript!=null) {
                    sketchScript.destroy();
                    sketchScript = null;
                }

            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                mOutputBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);

                if (mRenderScript==null) {
                    mRenderScript = RenderScript.create(BlurActivity.this);
                }
                if (sketchScript==null) {
                    sketchScript = new ScriptC_blur(mRenderScript);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                Allocation in = Allocation.createFromBitmap(mRenderScript,mBitmap);
                Allocation out = Allocation.createTyped(mRenderScript,in.getType());
                sketchScript.set_gWidth(mBitmap.getWidth());
                sketchScript.set_gHeight(mBitmap.getHeight());
                sketchScript.set_gIn(in);
                sketchScript.set_clickX(event.getX());
                sketchScript.set_clickY(event.getY());
                Log.e("nsc"," onProgressChanged x="+event.getX() + " y="+event.getY()
                        + " =="+mBitmap.getWidth() + " ="+ mBitmap.getHeight());
                sketchScript.set_radius(mProgress);
                sketchScript.forEach_invert(in,out);

                out.copyTo(mOutputBitmap);
                ivBlurredImage.setImageBitmap(mOutputBitmap);

                break;

            case MotionEvent.ACTION_UP:
                if (mRenderScript!=null){
                    mRenderScript.destroy();
                    mRenderScript = null;
                }
                if (sketchScript!=null) {
                    sketchScript.destroy();
                    sketchScript = null;
                }

                break;

        }
        return super.onTouchEvent(event);
    }

    public Bitmap fastBlur(Context context, Bitmap sentBitmap, int radius) {
        long time = System.currentTimeMillis();
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        Log.e("nsc", " setPixels=" + (System.currentTimeMillis() - time));
        return (bitmap);
    }

}
