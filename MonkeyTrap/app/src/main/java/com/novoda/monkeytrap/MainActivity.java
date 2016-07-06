package com.novoda.monkeytrap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch overlaySwitch = (Switch) findViewById(R.id.overlay_switch);
        overlaySwitch.setChecked(OverlayService.isOverlayShowing());
        overlaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(OverlayService.show());
                } else {
                    startService(OverlayService.hide());
                }
            }
        });
    }

}
