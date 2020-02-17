package com.novoda.tpbot.bot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.novoda.support.SelfDestructingMessageView;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.FeaturePersistence;
import com.novoda.tpbot.FeaturePersistenceFactory;
import com.novoda.tpbot.FeatureSelectionController;
import com.novoda.tpbot.R;
import com.novoda.tpbot.ServiceDeclarationListener;
import com.novoda.tpbot.bot.device.DeviceConnection;
import com.novoda.tpbot.bot.movement.MovementServiceBinder;
import com.novoda.tpbot.bot.service.BotServiceBinder;
import com.novoda.tpbot.bot.video.calling.AutomationChecker;
import com.novoda.tpbot.controls.CommandRepeater;
import com.novoda.tpbot.controls.ControllerListener;
import com.novoda.tpbot.controls.ControllerView;
import com.novoda.tpbot.controls.ServerDeclarationView;
import com.novoda.tpbot.controls.SwitchableView;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import dagger.android.AndroidInjection;

import static com.novoda.tpbot.controls.SwitchableView.View.CONTROLLER_VIEW;
import static com.novoda.tpbot.controls.SwitchableView.View.SERVER_DECLARATION_VIEW;

public class BotActivity extends AppCompatActivity implements BotView,
        DeviceConnection.DeviceConnectionListener,
        ControllerListener,
        ServiceDeclarationListener,
        CommandRepeater.Listener {

    private static final String HANGOUTS_BASE_URL = "https://hangouts.google.com/hangouts/_/novoda.com/";

    @Inject
    MovementServiceBinder movementServiceBinder;
    @Inject
    BotServiceBinder botServiceBinder;
    @Inject
    CommandRepeater commandRepeater;
    @Inject
    FeatureSelectionController<Menu, MenuItem> featureSelectionController;
    @Inject
    AutomationChecker automationChecker;
    @Inject
    FeaturePersistenceFactory featurePersistenceFactory;

    private FeaturePersistence videoCallFeature;
    private SelfDestructingMessageView debugView;
    private SwitchableView switchableView;
    private ControllerView controllerView;
    private ServerDeclarationView serverDeclarationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);

        debugView = findViewById(R.id.bot_controller_debug_view);
        switchableView = findViewById(R.id.bot_switchable_view);
        controllerView = findViewById(R.id.bot_controller_direction_view);
        serverDeclarationView = findViewById(R.id.bot_server_declaration_view);

        controllerView.setControllerListener(this);
        serverDeclarationView.setServiceDeclarationListener(this);

        videoCallFeature = featurePersistenceFactory.createVideoCallPersistence();
        FeaturePersistence serviceConnectionFeature = featurePersistenceFactory.createServiceConnectionPersistence();

        if (!serviceConnectionFeature.isEnabled()) {
            switchableView.switchTo(CONTROLLER_VIEW);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!automationChecker.isHangoutJoinerAutomationServiceEnabled() && videoCallFeature.isEnabled()) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }
    }

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

    @Override
    public void onServiceConnected(String serverAddress) {
        debugView.showPermanently(getString(R.string.connecting_ellipsis));
        botServiceBinder.bind(BotActivity.this, serverAddress);
    }

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

        if (videoCallFeature.isEnabled()) {
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

    @Override
    public void onCommandRepeated(String command) {
        debugView.showTimed(command);
        // TODO: Send command to the DeviceConnection.
    }
}
