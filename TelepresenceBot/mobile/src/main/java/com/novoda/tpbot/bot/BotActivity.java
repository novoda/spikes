package com.novoda.tpbot.bot;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.novoda.notils.caster.Views;
import com.novoda.notils.logger.toast.Toaster;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.R;
import com.novoda.tpbot.human.ControllerListener;
import com.novoda.tpbot.human.ControllerView;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BotActivity extends AppCompatActivity {

    private static final long COMMAND_REPEAT_DELAY = TimeUnit.MILLISECONDS.toMillis(100);

    private MovementService movementService;
    private boolean bound;
    private Handler handler;
    private String currentCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        RecyclerView directions = Views.findById(this, R.id.bot_directions);

        directions.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        directions.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new DirectionAdapter(LayoutInflater.from(this), Collections.<Direction>emptyList());
        directions.setAdapter(adapter);

        ControllerView controllerView = Views.findById(this, R.id.bot_debug_controls);
        controllerView.setControllerListener(controllerListener);

        handler = new Handler();
    }

    private final ControllerListener controllerListener = new ControllerListener() {

        @Override
        public void onDirectionPressed(Direction direction) {
            startRepeatingCommand(direction.rawCommand());
        }

        @Override
        public void onDirectionReleased(Direction direction) {
            stopRepeatingCommand(direction.rawCommand());
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

    private void startRepeatingCommand(String command) {
        currentCommand = command;
        sendCommand(currentCommand);
        handler.postDelayed(repeatCommand, COMMAND_REPEAT_DELAY);
    }

    private void stopRepeatingCommand(String command) {
        if (currentCommand != null && currentCommand.equals(command)) {
            handler.removeCallbacks(repeatCommand);
            currentCommand = null;
        }
    }

    private final Runnable repeatCommand = new Runnable() {
        @Override
        public void run() {
            sendCommand(BotActivity.this.currentCommand);
            handler.postDelayed(repeatCommand, COMMAND_REPEAT_DELAY);
        }
    };

    private void sendCommand(String command) {
        if (bound) {
            movementService.sendCommand(command);
        }
    }

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
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(serviceConnection);
            bound = false;
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder iBinder) {
            MovementService.Binder binder = (MovementService.Binder) iBinder;
            movementService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };
}
