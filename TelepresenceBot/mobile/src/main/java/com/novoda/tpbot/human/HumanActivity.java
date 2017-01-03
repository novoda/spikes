package com.novoda.tpbot.human;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.novoda.notils.caster.Views;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.R;
import com.novoda.tpbot.support.SelfDestructingMessageView;
import com.novoda.tpbot.support.SwitchableView;

import java.util.concurrent.TimeUnit;

public class HumanActivity extends AppCompatActivity implements HumanView {

    private static final String LAZERS = String.valueOf(Character.toChars(0x1F4A5));
    private static final long COMMAND_REPEAT_DELAY = TimeUnit.MILLISECONDS.toMillis(100);

    private SelfDestructingMessageView debugView;
    private SwitchableView switchableView;

    private Handler handler;
    private String currentCommand;

    private HumanPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human);

        presenter = new HumanPresenter(HumanSocketIOTpService.getInstance(), this);

        debugView = Views.findById(this, R.id.bot_controller_debug_view);
        switchableView = Views.findById(this, R.id.bot_switchable_view);

        handler = new Handler();

        ControllerView controllerView = Views.findById(switchableView, R.id.bot_controller_direction_view);
        controllerView.setControllerListener(controllerListener);

        ServerDeclarationView serverDeclarationView = Views.findById(switchableView, R.id.bot_server_declaration_view);
        serverDeclarationView.setServerDeclarationListener(serverDeclarationListener);
    }

    private final ControllerListener controllerListener = new ControllerListener() {

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
            startRepeatingCommand(LAZERS);
        }

        @Override
        public void onLazersReleased() {
            stopRepeatingCommand(LAZERS);
        }
    };

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

    private final ServerDeclarationListener serverDeclarationListener = new ServerDeclarationListener() {
        @Override
        public void onConnect(String serverAddress) {
            debugView.showPermanently(getResources().getString(R.string.connecting_ellipsis));
            presenter.startPresenting(serverAddress);
        }
    };

    @Override
    public void onConnect(String message) {
        debugView.showPermanently(getResources().getString(R.string.connected));
        switchableView.setDisplayedChild(1);
    }

    @Override
    public void onDisconnect() {
        switchableView.setDisplayedChild(0);
    }

    @Override
    public void onError(String message) {
        debugView.showPermanently(message);
        switchableView.setDisplayedChild(0);
    }

}
