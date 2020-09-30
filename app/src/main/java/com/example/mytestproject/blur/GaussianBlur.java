package com.example.mytestproject.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.FloatRange;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;

import com.coocent.photos.imagefilters.ScriptC_relief;

import java.lang.ref.WeakReference;

/**
 * Created by jrvansuita on 09/11/16.
 */

public class GaussianBlur {
    public static final int MIN_RADIUS = 0;
    public static final int MAX_RADIUS = 25;

    public static final int MIN_SIZE = 0;
    public static final int MAX_SIZE = 800;

    private Context context;
    private float mRadius;
    private float size;
    private int mCenterX = 1;
    private int mCenterY = 1;
    private int mTouchRadius;
    private int mViewWidth = 1;
    private int mViewHeight = 1;

    private GaussianBlur(Context context) {
        this.context = context;
        radius(MAX_RADIUS);
        size(MAX_SIZE);
    }

    public static GaussianBlur with(Context context) {
        return new GaussianBlur(context);
    }

    @WorkerThread
    public Bitmap render(int res) {
        return render(BitmapFactory.decodeResource(context.getResources(), res));
    }

    @WorkerThread
    public Bitmap render(Drawable drawable) {
        return render(((BitmapDrawable) drawable).getBitmap());
    }

    public int getViewWidth() {
        return mViewWidth;
    }

    public GaussianBlur setViewWidth(int mViewWidth) {
        this.mViewWidth = mViewWidth;
        return this;
    }

    public int getViewHeight() {
        return mViewHeight;
    }

    public GaussianBlur setViewHeight(int mViewHeight) {
        this.mViewHeight = mViewHeight;
        return this;
    }

    @WorkerThread
    public Bitmap render(Bitmap source) {
        Bitmap bitmap = source;
        RenderScript rs = RenderScript.create(context);

        ScriptC_relief sketchScript = new ScriptC_relief(rs);
        //sketchScript.set_size(parameter.getValue()/10);
        sketchScript.set_value((int)getRadius());
        Allocation in = Allocation.createFromBitmap(rs,bitmap);
        Allocation out = Allocation.createTyped(rs,in.getType());

        // call kernel
        // sketchScript.forEach_invert(in,out);
        sketchScript.set_value(mTouchRadius);
        sketchScript.set_inputAllocation(in);
        sketchScript.forEach_magnify(in,out);

        Log.e("TAG","apply ="+mTouchRadius);

        out.copyTo(bitmap);

        rs.destroy();
        sketchScript.destroy();
        in.destroy();
        out.destroy();


//        long start1 = System.currentTimeMillis();
//        if (getSize() > 0)
//            bitmap = scaleDown(bitmap);
//        long start2 = System.currentTimeMillis();
//
//        Log.e("nsc","render ="+(start2 -start1) + " getRadius="+getRadius());
//        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//
//        Allocation inAlloc = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_GRAPHICS_TEXTURE);
//        Allocation outAlloc = Allocation.createFromBitmap(rs, output);
//        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, inAlloc.getElement()); // Element.U8_4(rs));
//        script.setRadius(getRadius());
//        script.setInput(inAlloc);
//        script.forEach(outAlloc);
//        outAlloc.copyTo(output);
//
//        if (mViewWidth == 0) {
//            mViewWidth = 1;
//        }
//        if (mViewHeight == 0) {
//            mViewHeight = 1;
//        }
        //source=  blurByGauss(bitmap,5);
       // source = halo(bitmap, mCenterX * bitmap.getWidth() / mViewWidth, mCenterY * bitmap.getHeight() / mViewHeight, 80);
//        Log.e("nsc", "render mCenterX=" + mCenterX + " mCenterY=" + mCenterY + " mTouchRadius=" + mTouchRadius
//                + " getWidth=" + bitmap.getWidth() / 2 + " getHeight=" + bitmap.getHeight() / 2);
        //source = boxBlur(bitmap,1);
      //    bitmap = blurByGauss(bitmap,5);
       // rs.destroy();




        return bitmap;
    }

    public static Bitmap blurByGauss(Bitmap srcBitmap, int radius) {
        long start = System.currentTimeMillis();
        Bitmap bitmap = srcBitmap.copy(srcBitmap.getConfig(), true);
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
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

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
        long end = System.currentTimeMillis();
        Log.e("may", "used time=" + (end - start));
        return bitmap;
    }

    public void drawBitmap(Canvas canvas, Bitmap bitmap, boolean r, boolean g, boolean b) {
        ColorMatrix cm = new ColorMatrix();
        float[] m = cm.getArray();
        setColorFilterMatrix(m, r, g, b);
        Paint pt = new Paint();
        pt.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bitmap, 0, 0, pt);
    }


    public Bitmap handleEmbossEffect(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int color = 0, preColor = 0, a, r, g, b;
        int r1, g1, b1;
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int[] oldPx = new int[width * height];
        int[] newPx = new int[width * height];
        bitmap.getPixels(oldPx, 0, width, 0, 0, width, height);
        for (int i = 1; i < oldPx.length; i++) {
            preColor = oldPx[i - 1];
            a = Color.alpha(preColor);
            r = Color.red(preColor);
            g = Color.green(preColor);
            b = Color.blue(preColor);

            color = oldPx[i];
            r1 = Color.red(color);
            g1 = Color.green(color);
            b1 = Color.blue(color);

            r = r1 - r + 127;
            g = g1 - g + 127;
            b = b1 - b + 127;

            if (r > 255) {
                r = 255;
            } else if (r < 0) {
                r = 0;
            }

            if (g > 255) {
                g = 255;
            } else if (g < 0) {
                g = 0;
            }

            if (b > 255) {
                b = 255;
            } else if (b < 0) {
                b = 0;
            }
            newPx[i] = Color.argb(a, r, g, b);
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height);
        return bmp;
    }

    public void setColorFilterMatrix(float[] m, boolean r, boolean g, boolean b) {
        final float R = 0.213f;
        final float G = 0.715f;
        final float B = 0.072f;

        m[0] = 0;
        m[6] = 0;
        m[12] = 0;

        if (r) {
            m[0] = R;
            m[1] = G;
            m[2] = B;
        }
        if (g) {
            m[5] = R;
            m[6] = G;
            m[7] = B;
        }
        if (b) {
            m[10] = R;
            m[11] = G;
            m[12] = B;
        }
    }


    /**
     * 光晕效果
     *
     * @param bmp
     * @param x   光晕中心点在bmp中的x坐标
     * @param y   光晕中心点在bmp中的y坐标
     * @param r   光晕的半径
     * @return
     */
    public Bitmap halo(Bitmap bmp, int x, int y, float r) {
        long start = System.currentTimeMillis();
        // 高斯矩阵
        //float[] gauss = new float[]{1, 2, 1, 2, 4, 2, 1, 2, 1};
        float[] gauss = new float[]{1.90f, 2, 2, 2, 2, 2, 2, 2, 1.90f};
        float[] gauss1 = new float[]{1.95f, 2, 2, 2, 2, 2, 2, 2, 1.95f};
        float[] gauss2 = new float[]{1.98f, 2, 2, 2, 2, 2, 2, 2, 1.98f};
        float[] gauss3 = new float[]{2, 2, 2, 2, 2, 2, 2, 2, 2};//弱

        float[] gauss4 = new float[]{2.01f, 2, 2, 2, 2, 2, 2, 2, 2.01f};
        float[] gauss5 = new float[]{2.02f, 2, 2, 2, 2, 2, 2, 2, 2.02f};
        float[] gauss6 = new float[]{2.03f, 2, 2, 2, 2, 2, 2, 2, 2.03f};//弱
        float[] gauss7 = new float[]{2.05f, 2, 2, 2, 2, 2, 2, 2, 2.05f};//弱

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        //  Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int pixColor = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int delta = 18; // 值越小图片会越亮，越大则越暗  18
        int idx = 0;
        int[] pixels = new int[width * height];
        float radius = r * r;
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                idx = 0;

                int distance = (int) (Math.pow(k - x, 2) + Math.pow(i - y, 2));
                // 不是中心区域的点做模糊处理


                if (distance >= radius * 1.2f) {
                    setPixel(255, idx, i, width, k, gauss, pixels, 1.0f);
                } else if (distance >= radius * 1.1f) {

                    setPixel(250, idx, i, width, k, gauss1, pixels, 1.95f);
                } else if (distance >= radius * 1.0f) {

                    setPixel(245, idx, i, width, k, gauss2, pixels, 2.0f);
                } else if (distance >= radius * 0.9f) {
                    setPixel(240, idx, i, width, k, gauss3, pixels, 2.05f);
                } else if (distance >= radius * 0.8f) {

                    setPixel(235, idx, i, width, k, gauss4, pixels, 2.5f);
                } else if (distance >= radius * 7f) {

                    setPixel(230, idx, i, width, k, gauss5, pixels, 2.0f);
                } else if (distance >= radius * 0.6f) {
                    setPixel(225, idx, i, width, k, gauss6, pixels, 1);
                } else if (distance >= radius * 0.5f) {
                    setPixel(220, idx, i, width, k, gauss7, pixels, 2.005f);
                }
            }
        }

        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        long end = System.currentTimeMillis();
        Log.e("may", "used time=" + (end - start));
        return bmp;
    }


    private int[] setPixel(int alpha, int idx, int i,
                           int width, int k, float[] gauss, int[] pixels, float blur) {
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        float newR = 0;
        float newG = 0;
        float newB = 0;
        int pixColor = 0;
        for (int m = -1; m <= 1; m++) {
            for (int n = -1; n <= 1; n++) {
                pixColor = pixels[(i + m) * width + k + n];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);

                newR = newR +  (pixR * gauss[idx]);
                newG = newG +  (pixG * gauss[idx]);
                newB = newB +  (pixB * gauss[idx]);

                idx++;
            }
        }

        newR /= 18;
        newG /= 18;
        newB /= 18;
        newR = Math.min(255, Math.max(0, newR));
        newG = Math.min(255, Math.max(0, newG));
        newB = Math.min(255, Math.max(0, newB));
        pixels[i * width + k] = Color.argb(255, (int)newR, (int)newG, (int)newB);
        newR = 0;
        newG = 0;
        newB = 0;

        return pixels;
    }


    @UiThread
    public void put(Drawable drawable, ImageView imageView, int x, int y, int radius) {
        this.mCenterX = x;
        this.mCenterY = y;
        this.mTouchRadius = radius;
        new BitmapGaussianAsync(imageView).execute(((BitmapDrawable) drawable).getBitmap());
    }

    @UiThread
    public void put(Bitmap bitmap, ImageView imageView, int x, int y, int radius) {
        this.mCenterX = x;
        this.mCenterY = y;
        this.mTouchRadius = radius;
        new BitmapGaussianAsync(imageView).execute(bitmap);
    }

    @UiThread
    public void put(int res, ImageView imageView, int x, int y, int radius) {
        this.mCenterX = x;
        this.mCenterY = y;
        this.mTouchRadius = radius;
        new ResourceGaussianAsync(imageView).execute(res);
    }

    @WorkerThread
    public Bitmap scaleDown(int res) {
        return scaleDown(BitmapFactory.decodeResource(context.getResources(), res));
    }

    @WorkerThread
    public Bitmap scaleDown(Bitmap input) {
        float ratio = Math.min(getSize() / input.getWidth(), getSize() / input.getHeight());
        int width = Math.round(ratio * input.getWidth());
        int height = Math.round(ratio * input.getHeight());

        return Bitmap.createScaledBitmap(input, width, height, true);
    }

    public float getRadius() {
        return mRadius;
    }

    /**
     * @param radius Set the gaussian blur radius.
     */
    public GaussianBlur radius( float radius) {
        this.mRadius = radius;
        return this;
    }

    public float getSize() {
        return size;
    }

    /**
     * This method is provided to speed up the process. Once the image will be blurred,
     * there's no need to keep the original image size.
     * The smaller, the fastest.
     *
     * @param maxSize Set an float value to define the image size. Zero, means the image will be keep with original size.
     */
    public GaussianBlur size(@FloatRange(from = MIN_SIZE, to = MAX_SIZE) float maxSize) {
        this.size = maxSize;
        return this;
    }

    /**
     * Async load and apply gaussian blur on an image from resource
     */
    class ResourceGaussianAsync extends GaussianAsync<Integer> {

        public ResourceGaussianAsync(ImageView imageView) {
            super(imageView);
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            return render(params[0]);
        }
    }

    /**
     * Async load and apply gaussian blur on an image from bitmap
     */
    class BitmapGaussianAsync extends GaussianAsync<Bitmap> {

        public BitmapGaussianAsync(ImageView imageView) {
            super(imageView);
        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            return render(params[0]);
        }
    }


    /**
     * Async base class
     */
    abstract class GaussianAsync<T> extends AsyncTask<T, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public GaussianAsync(ImageView imageView) {
            imageViewReference = new WeakReference(imageView);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

}