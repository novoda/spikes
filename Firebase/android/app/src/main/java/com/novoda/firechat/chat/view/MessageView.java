package com.novoda.firechat.chat.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.novoda.firechat.R;
import com.novoda.firechat.chat.data.model.Message;
import com.novoda.notils.caster.Views;

public class MessageView extends LinearLayout {

    private ImageView picture;
    private TextView messageBody;

    public MessageView(Context context) {
        super(context);
        configureViewParams();
        View view = inflate(context, R.layout.merge_message_item_view, this);
        this.picture = Views.findById(view, R.id.messageAuthorImage);
        this.messageBody = Views.findById(view, R.id.messageBody);
    }

    private void configureViewParams() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.message_horizontal_margin);
        int verticalMargin = getResources().getDimensionPixelSize(R.dimen.message_vertical_margin);
        setPadding(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
    }

    public void display(Message message) {
        messageBody.setText(message.getBody());
        Context context = getContext();
        Glide.with(context).load(message.getAuthor().getPhotoUrl())
                .transform(new CircleCropTransformation(context))
                .into(picture);
    }

    private static class CircleCropTransformation extends BitmapTransformation {

        private final Paint paint = new Paint();

        public CircleCropTransformation(Context context) {
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
}
