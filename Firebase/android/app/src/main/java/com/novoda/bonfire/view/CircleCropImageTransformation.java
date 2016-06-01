package com.novoda.bonfire.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class CircleCropImageTransformation extends BitmapTransformation {

    private final Paint paint = new Paint();

    public CircleCropImageTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = getBitmap(pool, outWidth, outHeight);

        BitmapShader shader = new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        paint.setAntiAlias(true);
        paint.setShader(shader);

        RectF rect = new RectF(0.0f, 0.0f, outWidth, outHeight);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRoundRect(rect, outWidth / 2.0f, outHeight / 2.0f, paint);
        return bitmap;
    }

    private Bitmap getBitmap(BitmapPool pool, int outWidth, int outHeight) {
        Bitmap bitmap = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        }
        return bitmap;
    }

    @Override
    public String getId() {
        return "CircleCropTransformation";
    }
}
