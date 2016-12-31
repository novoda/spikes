package com.novoda.simonsaysandroidthings;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.things.pio.PeripheralManagerService;
import com.novoda.simonsaysandroidthings.game.SimonSays;
import com.novoda.simonsaysandroidthings.hw.board.BoardFactory;
import com.novoda.simonsaysandroidthings.hw.io.Button;
import com.novoda.simonsaysandroidthings.hw.io.Buzzer;
import com.novoda.simonsaysandroidthings.hw.io.GpioButton;
import com.novoda.simonsaysandroidthings.hw.io.GpioLed;
import com.novoda.simonsaysandroidthings.hw.io.Group;
import com.novoda.simonsaysandroidthings.hw.io.Led;
import com.novoda.simonsaysandroidthings.hw.io.PwmBuzzer;

import java.util.Collections;

public class HomeActivity extends Activity {

    private SimonSays game;
    private Button toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BoardFactory boardFactory = BoardFactory.getBoardFactory();
        PeripheralManagerService service = new PeripheralManagerService();
        Button yellowButton = GpioButton.create(boardFactory.getYellowButtonGpio(), service);
        Led yellowLed = GpioLed.create(boardFactory.getYellowLedGpio(), service);
        Buzzer buzzer = PwmBuzzer.create(boardFactory.getBuzzerPwm(), service);

        Group yellow = new Group(yellowLed, yellowButton, buzzer, 1000);

        game = new SimonSays(Collections.singletonList(yellow), new SharedPrefsHighscore(this), buzzer);
        game.start();

        toggleButton = GpioButton.create(boardFactory.getToggleGpio(), service);
        toggleButton.setListener(new Button.Listener() {
            @Override
            public void onButtonPressed(Button button) {
                // not used
            }

            @Override
            public void onButtonReleased(Button button) {
                game.toggle();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            game.close();
            toggleButton.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
