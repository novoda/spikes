package com.novoda.tpbot.bot;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityManager;

import com.novoda.notils.caster.Views;
import com.novoda.notils.logger.toast.Toaster;
import com.novoda.support.SelfDestructingMessageView;
import com.novoda.support.SwitchableView;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.R;
import com.novoda.tpbot.ServerDeclarationListener;
import com.novoda.tpbot.automation.AutomationChecker;
import com.novoda.tpbot.bot.device.DeviceConnection;
import com.novoda.tpbot.bot.device.usb.UsbDeviceConnection;
import com.novoda.tpbot.controls.CommandRepeater;
import com.novoda.tpbot.controls.ControllerListener;
import com.novoda.tpbot.controls.ControllerView;
import com.novoda.tpbot.controls.ServerDeclarationView;
import com.novoda.tpbot.feature_selection.FeatureSelectionPersistence;
import com.novoda.tpbot.feature_selection.ServerConnectionSharedPreferencesPersistence;
import com.novoda.tpbot.feature_selection.VideoCallSharedPreferencesPersistence;

import java.util.HashMap;

public class BotActivity extends AppCompatActivity implements BotView, DeviceConnection.DeviceConnectionListener {

    private static final String HANGOUTS_BASE_URL = "https://hangouts.google.com/hangouts/_/novoda.com/";

    private SelfDestructingMessageView debugView;
    private SwitchableView switchableView;

    private CommandRepeater commandRepeater;
    private AutomationChecker automationChecker;
    private BotServiceBinder botServiceBinder;
    private MovementServiceBinder movementServiceBinder;

    private FeatureSelectionPersistence videoCallFeature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoCallFeature = VideoCallSharedPreferencesPersistence.newInstance(this);
        FeatureSelectionPersistence serverConnectionFeature = ServerConnectionSharedPreferencesPersistence.newInstance(this);

        setContentView(R.layout.activity_bot);

        debugView = Views.findById(this, R.id.bot_controller_debug_view);
        switchableView = Views.findById(this, R.id.bot_switchable_view);

        ControllerView controllerView = Views.findById(this, R.id.bot_controller_direction_view);
        controllerView.setControllerListener(controllerListener);

        ServerDeclarationView serverDeclarationView = Views.findById(switchableView, R.id.bot_server_declaration_view);
        serverDeclarationView.setServerDeclarationListener(serverDeclarationListener);

        Handler handler = new Handler();
        commandRepeater = new CommandRepeater(commandRepeatedListener, handler);

        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        automationChecker = new AutomationChecker(accessibilityManager);

        DeviceConnection deviceConnection = UsbDeviceConnection.newInstance(getApplicationContext(), this);

        botServiceBinder = new BotServiceBinder(getApplicationContext());
        movementServiceBinder = new MovementServiceBinder(getApplicationContext(), deviceConnection);

        if (!serverConnectionFeature.isFeatureEnabled()) {
            switchableView.setDisplayedChild(1);
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

    private final ServerDeclarationListener serverDeclarationListener = new ServerDeclarationListener() {

        @Override
        public void onConnect(String serverAddress) {
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bot_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.usb_devices_list_menu_item:
                showConnectedDevices();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showConnectedDevices() {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        StringBuilder builder = new StringBuilder();
        if (devices.isEmpty()) {
            builder.append(getString(R.string.no_connected_devices));
        } else {
            for (UsbDevice device : devices.values()) {
                builder.append(
                        getString(
                                R.string.usb_device_name_vendor_product,
                                device.getDeviceName(),
                                device.getVendorId(),
                                device.getProductId()
                        )
                ).append("\n");
            }
        }
        Toaster.newInstance(this).popBurntToast(builder.toString());
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
        switchableView.setDisplayedChild(1);

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
        switchableView.setDisplayedChild(0);
    }

    @Override
    public void onError(String message) {
        debugView.showPermanently(message);
        switchableView.setDisplayedChild(0);
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
