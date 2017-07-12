package com.novoda.simonsaysandroidthings;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.simonsaysandroidthings.game.Score;
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

    private ValueEventListener valueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BoardFactory boardFactory = BoardFactory.getBoardFactory();
        PeripheralManagerService service = new PeripheralManagerService();
        Buzzer buzzer = PwmBuzzer.create(boardFactory.getBuzzerPwm(), service);

        Group green = createGroup(service, boardFactory.getGreenButtonGpio(), boardFactory.getGreenLedGpio(), buzzer, 500);
        Group red = createGroup(service, boardFactory.getRedButtonGpio(), boardFactory.getRedLedGpio(), buzzer, 1000);
        Group blue = createGroup(service, boardFactory.getBlueButtonGpio(), boardFactory.getBlueLedGpio(), buzzer, 1500);
        Group yellow = createGroup(service, boardFactory.getYellowButtonGpio(), boardFactory.getYellowLedGpio(), buzzer, 2000);

//        Highscore highscore = new SharedPrefsHighscore(this);
        Score score = new FirebaseScore(FirebaseDatabase.getInstance().getReference());
        game = new SimonSays(Arrays.asList(green, red, blue, yellow), score, buzzer);
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

        final TextView hishcore = (TextView) findViewById(R.id.highscore);
        final TextView currentRound = (TextView) findViewById(R.id.current_round);

        valueEventListener = FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hishcore.setText(dataSnapshot.child("highscore").getValue(Long.class).toString());
                currentRound.setText(dataSnapshot.child("current_score").getValue(Long.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // nothing to do
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
        FirebaseDatabase.getInstance().getReference().removeEventListener(valueEventListener);

        try {
            game.close();
            toggleButton.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
