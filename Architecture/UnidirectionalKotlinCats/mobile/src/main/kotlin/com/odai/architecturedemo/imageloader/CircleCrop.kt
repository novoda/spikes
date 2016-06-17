package com.odai.architecturedemo.imageloader

import android.content.Context
import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

class CircleCrop(context: Context) : BitmapTransformation(context) {

    val paint = Paint()

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        var bitmap = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        }

        val shader = BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        paint.isAntiAlias = true;
        paint.shader = shader;

        val rect = RectF(0.0f, 0.0f, outWidth.toFloat(), outHeight.toFloat());
        val canvas = Canvas(bitmap);
        canvas.drawRoundRect(rect, outWidth / 2.0f, outHeight / 2.0f, paint);
        return bitmap;
    }

    override fun getId() = "CircleCrop"

}
