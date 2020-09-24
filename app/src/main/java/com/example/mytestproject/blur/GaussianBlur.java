package com.example.mytestproject.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

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
    private int radius;
    private float size;
    private int mCenterX;
    private int mCenterY;
    private int mTouchRadius;
    private int mViewWidth;
    private int mViewHeight;

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

        if (getSize() > 0)
            bitmap = scaleDown(bitmap);

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Allocation inAlloc = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_GRAPHICS_TEXTURE);
        Allocation outAlloc = Allocation.createFromBitmap(rs, output);
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, inAlloc.getElement()); // Element.U8_4(rs));
        script.setRadius(getRadius());
        script.setInput(inAlloc);
        script.forEach(outAlloc);
        outAlloc.copyTo(output);

        //  source = halo(bitmap, mCenterX * bitmap.getWidth() / mViewWidth, mCenterY * bitmap.getHeight() / mViewHeight, mTouchRadius);
//        Log.e("nsc", "render mCenterX=" + mCenterX + " mCenterY=" + mCenterY + " mTouchRadius=" + mTouchRadius
//                + " getWidth=" + bitmap.getWidth() / 2 + " getHeight=" + bitmap.getHeight() / 2);
        //source = boxBlur(bitmap,1);
        rs.destroy();
        return output;
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
        float[] gauss = new float[]{1.95f, 2, 2, 2, 2, 2, 2, 2, 1.95f};
        float[] gauss1 = new float[]{2, 2, 2, 2, 2, 2, 2, 2, 1.95f};
        float[] gauss2 = new float[]{2, 2, 2, 2, 2, 2, 2, 2, 1.95f};
        float[] gauss3 = new float[]{2, 2, 2, 2, 2, 2, 2, 2, 2};//弱

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

                if (distance >= radius * 1.3f) {
                    setPixel(idx, i, width, k, gauss, pixels);
//                    for (int m = -1; m <= 1; m++) {
//                        for (int n = -1; n <= 1; n++) {
//                            pixColor = pixels[(i + m) * width + k + n];
//                            pixR = Color.red(pixColor);
//                            pixG = Color.green(pixColor);
//                            pixB = Color.blue(pixColor);
//
//                            newR = newR + (int) (pixR * gauss[idx]);
//                            newG = newG + (int) (pixG * gauss[idx]);
//                            newB = newB + (int) (pixB * gauss[idx]);
//                            idx++;
//                        }
//                    }
//
//                    newR /= delta;
//                    newG /= delta;
//                    newB /= delta;
//                    newR = Math.min(255, Math.max(0, newR));
//                    newG = Math.min(255, Math.max(0, newG));
//                    newB = Math.min(255, Math.max(0, newB));
//                    pixels[i * width + k] = Color.argb(255, newR, newG, newB);
//                    newR = 0;
//                    newG = 0;
//                    newB = 0;
                } else if (distance >= radius * 1.1f) {

                    setPixel(idx, i, width, k, gauss1, pixels);
                } else if (distance >= radius * 9f) {

                    setPixel(idx, i, width, k, gauss2, pixels);
                } else if (distance >= radius * 0.7f) {
                    setPixel(idx, i, width, k, gauss3, pixels);
                }
            }
        }

        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        long end = System.currentTimeMillis();
        Log.e("may", "used time=" + (end - start));
        return bmp;
    }

    private int[] setPixel(int idx, int i,
                           int width, int k, float[] gauss, int[] pixels) {
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int pixColor = 0;
        for (int m = -1; m <= 1; m++) {
            for (int n = -1; n <= 1; n++) {
                pixColor = pixels[(i + m) * width + k + n];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);

                newR = newR + (int) (pixR * gauss[idx]);
                newG = newG + (int) (pixG * gauss[idx]);
                newB = newB + (int) (pixB * gauss[idx]);
                idx++;
            }
        }

        newR /= 18;
        newG /= 18;
        newB /= 18;
        newR = Math.min(255, Math.max(0, newR));
        newG = Math.min(255, Math.max(0, newG));
        newB = Math.min(255, Math.max(0, newB));
        pixels[i * width + k] = Color.argb(255, newR, newG, newB);
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

    public int getRadius() {
        return radius;
    }

    /**
     * @param radius Set the gaussian blur radius.
     */
    public GaussianBlur radius(@IntRange(from = MIN_RADIUS, to = MAX_RADIUS) int radius) {
        this.radius = radius;
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