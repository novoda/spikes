package com.novoda.androidp.pt651_dispplay_cutout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;

import com.novoda.androidp.R;

public class CutoutActivity extends AppCompatActivity {

    private View linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutout);
        
//        WindowManager.LayoutParams attributes = getWindow().getAttributes();
//        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;

        linearLayout = findViewById(R.id.main_layout);

        linearLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                DisplayCutout displayCutout = windowInsets.getDisplayCutout();
                return windowInsets;
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            linearLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                       | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                       | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                       | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                       | View.SYSTEM_UI_FLAG_FULLSCREEN
                                                       | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
