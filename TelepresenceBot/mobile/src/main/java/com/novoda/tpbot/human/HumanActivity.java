package com.novoda.tpbot.human;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.R;
import com.novoda.tpbot.SelfDestructingMessageView;

public class HumanActivity extends AppCompatActivity {

    private static final String LAZERS = String.valueOf(Character.toChars(0x1F4A5));

    private static final long DURATION_DIRECTIONS = 1500L;
    private static final long DURATION_LAZERS = 150L;

    private SelfDestructingMessageView debugView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human);
        debugView = Views.findById(this, R.id.bot_controller_debug_view);

        ControllerView controllerView = Views.findById(this, R.id.bot_controller_direction_view);
        controllerView.setControllerListener(new ControllerListener() {

            @Override
            public void onDirectionPressed(Direction direction) {
                debugView.showPermanently(direction.getRepresentation());
            }

            @Override
            public void onDirectionReleased(Direction direction) {
                debugView.showTimed(direction.getRepresentation() + " released", DURATION_DIRECTIONS);
            }

            @Override
            public void onLazersFired() {
                debugView.showTimed(LAZERS, DURATION_LAZERS);
            }

            @Override
            public void onLazersReleased() {
                debugView.clearMessage();
            }
        });
    }
}
