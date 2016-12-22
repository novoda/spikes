package com.novoda.tpbot.human;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.R;
import com.novoda.tpbot.SelfDestructingMessageView;
import com.novoda.tpbot.support.SwitchableView;

import java.util.concurrent.TimeUnit;

public class HumanActivity extends AppCompatActivity {

    private static final String LAZERS = String.valueOf(Character.toChars(0x1F4A5));

    private static final long COMMAND_REPEAT_DELAY = TimeUnit.MILLISECONDS.toMillis(100);

    private Handler handler;
    private SelfDestructingMessageView debugView;

    String currentCommand;
    private SwitchableView switchableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human);
        debugView = Views.findById(this, R.id.bot_controller_debug_view);

        handler = new Handler();

        switchableView = Views.findById(this, R.id.bot_switchable_view);

        ControllerView controllerView = Views.findById(this, R.id.bot_controller_direction_view);
        controllerView.setControllerListener(new ControllerListener() {

            @Override
            public void onDirectionPressed(Direction direction) {
                startRepeatingCommand(direction.visualRepresentation());
            }

            @Override
            public void onDirectionReleased(Direction direction) {
                stopRepeatingCommand(direction.visualRepresentation());
            }

            @Override
            public void onLazersFired() {
                switchableView.showNext();
                startRepeatingCommand(LAZERS);
            }

            @Override
            public void onLazersReleased() {
                stopRepeatingCommand(LAZERS);
            }
        });
    }

    private void startRepeatingCommand(String command) {
        currentCommand = command;
        debugView.showTimed(currentCommand, COMMAND_REPEAT_DELAY);
        handler.postDelayed(repeatCommand, COMMAND_REPEAT_DELAY);
    }

    private void stopRepeatingCommand(String command) {
        if (currentCommand != null && currentCommand.equals(command)) {
            handler.removeCallbacks(repeatCommand);
            currentCommand = null;
        }
    }

    private Runnable repeatCommand = new Runnable() {
        @Override
        public void run() {
            debugView.showTimed(currentCommand, COMMAND_REPEAT_DELAY);
            handler.postDelayed(repeatCommand, COMMAND_REPEAT_DELAY);
        }
    };

    @Override
    protected void onPause() {
        stopRepeatingCommand(currentCommand);
        super.onPause();
    }
}
