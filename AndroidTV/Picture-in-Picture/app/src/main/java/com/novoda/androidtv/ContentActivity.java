package com.novoda.androidtv;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.text.format.DateUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.novoda.notils.caster.Views;

/**
 * <a href="https://www.youtube.com/watch?v=TxAbht2DkyU">Picture in Picture on Android TV (Android Development Patterns S3 Ep2)</a>
 */
public class ContentActivity extends Activity {

    private static final Uri RANDOM_IMAGE_URI = Uri.parse("http://loremflickr.com/320/240/");
    private static final long REPEAT_DELAY = 5 * DateUtils.SECOND_IN_MILLIS;

    private View pipButton;
    private Handler handler;
    private BackgroundManager backgroundManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_in_picture);
        pipButton = Views.findById(this, R.id.content_button_pip);
        pipButton.setOnClickListener(onPipButtonClickListener);

        handler = new Handler();
        backgroundManager = BackgroundManager.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isInPictureInPictureMode()) {
            backgroundManager.attach(getWindow());
            handler.postDelayed(updateBackgroundRunnable, REPEAT_DELAY);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isInPictureInPictureMode()) {
            // stop loading new images if we are not visible
            handler.removeCallbacks(updateBackgroundRunnable);
            backgroundManager.release();
        }
    }

    private final Runnable updateBackgroundRunnable = new Runnable() {
        @Override
        public void run() {
            loadARandomBackgroundImage();
            handler.postDelayed(this, REPEAT_DELAY);
        }
    };

    private void loadARandomBackgroundImage() {
        Glide.with(this)
                .load(RANDOM_IMAGE_URI)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(800, 600) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        backgroundManager.setBitmap(bitmap);
                    }
                });
    }

    private final View.OnClickListener onPipButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enterPictureInPictureMode();
        }
    };

}
