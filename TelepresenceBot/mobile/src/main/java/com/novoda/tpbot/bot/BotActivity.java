package com.novoda.tpbot.bot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityManager;

import com.novoda.notils.caster.Views;
import com.novoda.support.SelfDestructingMessageView;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.FeatureSelectionPersistence;
import com.novoda.tpbot.R;
import com.novoda.tpbot.ServiceDeclarationListener;
import com.novoda.tpbot.bot.device.DeviceConnection;
import com.novoda.tpbot.bot.movement.MovementServiceBinder;
import com.novoda.tpbot.bot.service.BotServiceBinder;
import com.novoda.tpbot.bot.service.ServiceConnectionSharedPreferencesPersistence;
import com.novoda.tpbot.bot.video.calling.AutomationChecker;
import com.novoda.tpbot.bot.video.calling.VideoCallSharedPreferencesPersistence;
import com.novoda.tpbot.controls.CommandRepeater;
import com.novoda.tpbot.controls.ControllerListener;
import com.novoda.tpbot.controls.ControllerView;
import com.novoda.tpbot.controls.ServerDeclarationView;
import com.novoda.tpbot.controls.SwitchableView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.novoda.tpbot.controls.SwitchableView.View.CONTROLLER_VIEW;
import static com.novoda.tpbot.controls.SwitchableView.View.SERVER_DECLARATION_VIEW;

public class BotActivity extends AppCompatActivity implements BotView, DeviceConnection.DeviceConnectionListener {

    private static final String HANGOUTS_BASE_URL = "https://hangouts.google.com/hangouts/_/novoda.com/";

    private SelfDestructingMessageView debugView;
    private SwitchableView switchableView;

    private CommandRepeater commandRepeater;
    private AutomationChecker automationChecker;
    private BotServiceBinder botServiceBinder;
    private MovementServiceBinder movementServiceBinder;

    private FeatureSelectionPersistence videoCallFeature;

    @Inject
    DeviceConnection deviceConnection;

    @Inject
    FeatureSelectionController<Menu, MenuItem> featureSelectionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        AndroidInjection.inject(this);

        videoCallFeature = VideoCallSharedPreferencesPersistence.newInstance(this);
        FeatureSelectionPersistence serverConnectionFeature = ServiceConnectionSharedPreferencesPersistence.newInstance(this);

        debugView = Views.findById(this, R.id.bot_controller_debug_view);
        switchableView = Views.findById(this, R.id.bot_switchable_view);

        ControllerView controllerView = Views.findById(this, R.id.bot_controller_direction_view);
        controllerView.setControllerListener(controllerListener);

        ServerDeclarationView serverDeclarationView = Views.findById(switchableView, R.id.bot_server_declaration_view);
        serverDeclarationView.setServiceDeclarationListener(serviceDeclarationListener);

        Handler handler = new Handler();
        commandRepeater = new CommandRepeater(commandRepeatedListener, handler);

        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        automationChecker = new AutomationChecker(accessibilityManager);

        botServiceBinder = new BotServiceBinder(getApplicationContext());
        movementServiceBinder = new MovementServiceBinder(getApplicationContext(), deviceConnection);

        if (!serverConnectionFeature.isFeatureEnabled()) {
            switchableView.switchTo(CONTROLLER_VIEW);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!automationChecker.isHangoutJoinerAutomationServiceEnabled() && videoCallFeature.isFeatureEnabled()) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }
    }

    private final ControllerListener controllerListener = new ControllerListener() {

        @Override
        public void onDirectionPressed(Direction direction) {
            commandRepeater.startRepeatingCommand(direction.rawDirection());
        }

        @Override
        public void onDirectionReleased(Direction direction) {
            commandRepeater.stopRepeatingCommand(direction.rawDirection());
        }

        @Override
        public void onLazersFired() {
            // no-op for debug
        }

        @Override
        public void onLazersReleased() {
            // no-op for debug
        }
    };

    private final ServiceDeclarationListener serviceDeclarationListener = new ServiceDeclarationListener() {

        @Override
        public void onServiceConnected(String serverAddress) {
            debugView.showPermanently(getString(R.string.connecting_ellipsis));
            botServiceBinder.bind(BotActivity.this, serverAddress);
        }
    };

    private CommandRepeater.Listener commandRepeatedListener = new CommandRepeater.Listener() {
        @Override
        public void onCommandRepeated(String command) {
            // TODO: Send command to the DeviceConnection.
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        movementServiceBinder.bind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        featureSelectionController.attachFeaturesTo(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (featureSelectionController.contains(item)) {
            featureSelectionController.handleFeatureToggle(item);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        commandRepeater.stopCurrentRepeatingCommand();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (botServiceBinder != null) {
            botServiceBinder.unbind();
            botServiceBinder = null;
        }

        if (movementServiceBinder != null) {
            movementServiceBinder.unbind();
            movementServiceBinder = null;
        }
        super.onDestroy();
    }

    @Override
    public void onConnect(String room, String serverAddress) {
        debugView.showPermanently(getString(R.string.connected));
        switchableView.switchTo(CONTROLLER_VIEW);

        if (videoCallFeature.isFeatureEnabled()) {
            joinHangoutRoom(room);
        }
    }

    private void joinHangoutRoom(String room) {
        String url = HANGOUTS_BASE_URL + room;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onDisconnect() {
        debugView.showPermanently(getString(R.string.disconnected));
        switchableView.switchTo(SERVER_DECLARATION_VIEW);
    }

    @Override
    public void onError(String message) {
        debugView.showPermanently(message);
        switchableView.switchTo(SERVER_DECLARATION_VIEW);
    }

    @Override
    public void moveIn(Direction direction) {
        debugView.showTimed(direction.visualRepresentation());
        commandRepeatedListener.onCommandRepeated(direction.rawDirection());
    }

    @Override
    public void onDeviceConnected() {
        Log.d(getClass().getSimpleName(), "onDeviceConnected");
    }

    @Override
    public void onDeviceDisconnected() {
        Log.d(getClass().getSimpleName(), "onDeviceDisconnected");
    }

    @Override
    public void onDataReceived(String data) {
        Log.d(getClass().getSimpleName(), "onDataReceived");
    }
}
