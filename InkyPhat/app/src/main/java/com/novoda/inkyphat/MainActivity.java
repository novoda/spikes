package com.novoda.inkyphat;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    private static final String INKY_PHAT_DISPLAY = "SPI0.0";
    private static final String COMMAND_PIN = "BCM22";
    private static final String RESET_PIN = "BCM27";
    private static final String BUSY_PIN = "BCM17";

    private InkyPhat inkyPhat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inkyPhat = InkyPhat.create(INKY_PHAT_DISPLAY, BUSY_PIN, RESET_PIN, COMMAND_PIN);
//        Tests.drawTwoSquares(inkyPhat);
        Tests.drawImage(inkyPhat, getResources());
        inkyPhat.refresh();
    }

    @Override
    protected void onDestroy() {
        inkyPhat.close();
        super.onDestroy();
    }
}
