package com.novoda.simonsaysandroidthings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.novoda.simonsaysandroidthings.hw.board.BoardFactory;
import com.novoda.simonsaysandroidthings.hw.io.Button;
import com.novoda.simonsaysandroidthings.hw.io.Buzzer;
import com.novoda.simonsaysandroidthings.hw.io.GpioButton;
import com.novoda.simonsaysandroidthings.hw.io.GpioLed;
import com.novoda.simonsaysandroidthings.hw.io.Group;
import com.novoda.simonsaysandroidthings.hw.io.Led;
import com.novoda.simonsaysandroidthings.hw.io.PwmBuzzer;

public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";
    private Group yellow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BoardFactory boardFactory = BoardFactory.getBoardFactory();
        PeripheralManagerService service = new PeripheralManagerService();

        Button yellowButton = GpioButton.create(boardFactory.getYellowButtonGpio(), service);
        Led yellowLed = GpioLed.create(boardFactory.getYellowLedGpio(), service);
        Buzzer buzzer = PwmBuzzer.create(boardFactory.getBuzzerPwm(), service);

        yellow = new Group(yellowLed, yellowButton, buzzer, 1000);

        yellowButton.setListener(new Button.Listener() {
            @Override
            public void onButtonPressed(Button button) {
                Log.d(TAG, "onButtonPressed() called");
                yellow.play();
            }

            @Override
            public void onButtonReleased(Button button) {
                Log.d(TAG, "onButtonReleased() called");
                yellow.stop();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            yellow.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
