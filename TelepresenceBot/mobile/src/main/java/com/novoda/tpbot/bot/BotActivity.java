package com.novoda.tpbot.bot;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.novoda.notils.caster.Views;
import com.novoda.notils.logger.toast.Toaster;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.R;
import com.novoda.tpbot.controls.CommandRepeater;
import com.novoda.tpbot.controls.ControllerListener;
import com.novoda.tpbot.controls.ControllerView;
import com.novoda.tpbot.human.ServerDeclarationView;
import com.novoda.support.SelfDestructingMessageView;
import com.novoda.tpbot.ServerDeclarationListener;
import com.novoda.support.SwitchableView;

import java.util.HashMap;

public class BotActivity extends AppCompatActivity implements BotView {

    private static final String HANGOUTS_BASE_URL = "https://hangouts.google.com/hangouts/_/novoda.com/";
    private SelfDestructingMessageView debugView;
    private SwitchableView switchableView;

    private MovementService movementService;
    private boolean boundToMovementService;
    private CommandRepeater commandRepeater;
    private BotPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);

        debugView = Views.findById(this, R.id.bot_controller_debug_view);
        switchableView = Views.findById(this, R.id.bot_switchable_view);

        presenter = new BotPresenter(SocketIOTelepresenceService.getInstance(), this);

        ControllerView controllerView = Views.findById(this, R.id.bot_controller_direction_view);
        controllerView.setControllerListener(controllerListener);

        ServerDeclarationView serverDeclarationView = Views.findById(switchableView, R.id.bot_server_declaration_view);
        serverDeclarationView.setServerDeclarationListener(serverDeclarationListener);

        Handler handler = new Handler();
        commandRepeater = new CommandRepeater(commandRepeatedListener, handler);
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
            presenter.startPresenting(serverAddress);
        }
    };

    private CommandRepeater.Listener commandRepeatedListener = new CommandRepeater.Listener() {
        @Override
        public void onCommandRepeated(String command) {
            if (boundToMovementService) {
                movementService.sendCommand(command);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MovementService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
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
    protected void onStop() {
        if (boundToMovementService) {
            unbindService(serviceConnection);
            boundToMovementService = false;
        }
        presenter.stopPresenting();
        super.onStop();
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MovementService.Binder binder = (MovementService.Binder) iBinder;
            movementService = binder.getService();
            boundToMovementService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            boundToMovementService = false;
        }
    };

    @Override
    public void onConnect(String room) {
        debugView.showPermanently(getString(R.string.connected));
        switchableView.setDisplayedChild(1);

        joinHangoutRoom(room);
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
    }

}
