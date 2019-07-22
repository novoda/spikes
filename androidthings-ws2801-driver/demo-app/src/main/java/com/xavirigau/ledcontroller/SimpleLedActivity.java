package com.xavirigau.ledcontroller;

import com.xrigau.driver.ws2801.Ws2801;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class SimpleLedActivity extends Activity {

    private static final String TAG = RainbowLedActivity.class.getSimpleName();

    private Ws2801 mLedstrip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mLedstrip = Ws2801.create(BoardDefaults.getSPIPort(), Ws2801.Mode.RGB);
            mLedstrip.write(new int[]{Color.parseColor("#0face0")});
            Log.d(TAG, "Done!");
        } catch (IOException e) {
            Log.e(TAG, "Error initializing LED strip", e);
        }
    }

    @Override
    public void onDestroy() {
        try {
            mLedstrip.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception closing LED strip", e);
        }
        super.onDestroy();
    }

}

