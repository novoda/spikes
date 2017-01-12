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

import java.util.Arrays;

public class HomeActivity extends Activity {

    private SimonSays game;
    private Button toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BoardFactory boardFactory = BoardFactory.getBoardFactory();
        PeripheralManagerService service = new PeripheralManagerService();
        Buzzer buzzer = PwmBuzzer.create(boardFactory.getBuzzerPwm(), service);

        Group green = createGroup(service, boardFactory.getGreenButtonGpio(), boardFactory.getGreenLedGpio(), buzzer, 500);
        Group red = createGroup(service, boardFactory.getRedButtonGpio(), boardFactory.getRedLedGpio(), buzzer, 1000);
        Group blue = createGroup(service, boardFactory.getBlueButtonGpio(), boardFactory.getBlueLedGpio(), buzzer, 1500);
        Group yellow = createGroup(service, boardFactory.getYellowButtonGpio(), boardFactory.getYellowLedGpio(), buzzer, 2000);

        game = new SimonSays(Arrays.asList(green, red, blue, yellow), new SharedPrefsHighscore(this), buzzer);
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

    private Group createGroup(PeripheralManagerService service, String buttonPinName, String ledPinName, Buzzer buzzer, int frequency) {
        Button button = GpioButton.create(buttonPinName, service);
        Led led = GpioLed.create(ledPinName, service);
        return new Group(led, button, buzzer, frequency);
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
