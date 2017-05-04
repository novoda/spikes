package com.novoda.pianohero;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;
import com.novoda.pianohero.hax.RGBmatrixPanel;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Device Descriptor:
 * Device Class: (Defined Per Interface) -> Subclass: 0x00 -> Protocol: 0x00
 * Max Packet Size: 8
 * VID: 0x0a4d
 * PID: 0x129d
 * <p>
 * <p>
 * Configuration Descriptor:
 * 2 Interfaces
 * Attributes: BusPowered
 * Max Power: 64mA
 * -- Interface 0 Class: Audio -> Subclass: 0x01 -> Protocol: 0x00, 0 Endpoints
 * -- Interface 1 Class: Audio -> Subclass: 0x03 -> Protocol: 0x00, 2 Endpoints
 * null
 * null
 */
public class AndroidThingsActivity extends AppCompatActivity implements GameMvp.View {

    private GameMvp.Presenter presenter;
    private Handler handler;
    private UartDevice bus;
    private MidiManager midiManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");

        PeripheralManagerService service = new PeripheralManagerService();

        for (String device : service.getUartDeviceList()) {
            Log.d("!!!", "UART device on pin:" + device);
        }

        midiManager = (MidiManager) getSystemService(Context.MIDI_SERVICE);
        try {
            bus = service.openUartDevice("UART0");
        } catch (IOException e) {
            throw new IllegalStateException("Cannot open bus to input peripheral.", e);
        }

        haxUpdateDisplay();

        HandlerThread thread = new HandlerThread("BackgroundThread");
        thread.start();
        handler = new Handler(thread.getLooper());

        // TODO hax
        handler.post(new Runnable() {
            @Override
            public void run() {
                haxUpdateDisplay();
            }
        });

        GameMvp.Model gameModel = new GameModel(new SongSequenceFactory(), new SimplePitchNotationFormatter());
        presenter = new GamePresenter(gameModel, this);
        presenter.onCreate();
    }

    private void haxUpdateDisplay() {
        PeripheralManagerService service = new PeripheralManagerService();
        try {
            Gpio busOutputEnabled = service.openGpio("BCM2");
            Gpio busSerialClock = service.openGpio("BCM3");
            Gpio busDataLatch = service.openGpio("BCM4");
            Gpio busRowAddressA = service.openGpio("BCM7");
            Gpio busRowAddressB = service.openGpio("BCM8");
            Gpio busRowAddressC = service.openGpio("BCM19");
            Gpio busRowAddressD = service.openGpio("BCM20");
            Gpio busLedR1 = service.openGpio("BCM17");
            Gpio busLedB1 = service.openGpio("BCM18");
            Gpio busLedG1 = service.openGpio("BCM22");
            Gpio busLedR2 = service.openGpio("BCM23");
            Gpio busLedB2 = service.openGpio("BCM24");
            Gpio busLedG2 = service.openGpio("BCM25");

            busOutputEnabled.setValue(true);
            busSerialClock.setValue(true);
            busDataLatch.setValue(true);

            busRowAddressA.setValue(true);
            busRowAddressB.setValue(true);
            busRowAddressC.setValue(true);
            busRowAddressD.setValue(true);

            for (int row = 0; row < 16; ++row) {
                // Rows can't be switched very quickly without ghosting, so we do the
                // full PWM of one row before switching rows.
                for (int b = 0; b < 7; b++) {

                    // Clock in the row. The time this takes is the smallest time we can
                    // leave the LEDs on, thus the smallest time-constant we can use for
                    // PWM (doubling the sleep time with each bit).
                    // So this is the critical path; I'd love to know if we can employ some
                    // DMA techniques to speed this up.
                    // (With this code, one row roughly takes 3.0 - 3.4usec to clock in).
                    //
                    // However, in particular for longer chaining, it seems we need some more
                    // wait time to settle.
                    long stabilizeWait = TimeUnit.NANOSECONDS.toMillis(256); //TODO: mateo was 256

                    for (int col = 0; col < 32; ++col) {

                        busOutputEnabled.setValue(false);
                        busSerialClock.setValue(false);
                        busDataLatch.setValue(false);

                        busRowAddressA.setValue(false);
                        busRowAddressB.setValue(false);
                        busRowAddressC.setValue(false);
                        busRowAddressD.setValue(false);

                        busLedR1.setValue(false);
                        busLedG1.setValue(false);
                        busLedB1.setValue(false);
                        busLedR2.setValue(false);
                        busLedG2.setValue(false);
                        busLedB2.setValue(false);

                        SystemClock.sleep(stabilizeWait);

                        busLedR1.setValue(true);
                        busLedG1.setValue(true);
                        busLedB1.setValue(true);
                        busLedR2.setValue(true);
                        busLedG2.setValue(true);
                        busLedB2.setValue(true);

                        SystemClock.sleep(stabilizeWait);

                        busSerialClock.setValue(true);

                        SystemClock.sleep(stabilizeWait);
                    }

                    // switch off while strobe (latch).
                    busOutputEnabled.setValue(true);

                    // rowAddress is 4 bits
                    if ((row & 8) == 8) {
                        busRowAddressD.setValue(true);
                    }
                    if ((row & 4) == 4) {
                        busRowAddressC.setValue(true);
                    }
                    if ((row & 2) == 2) {
                        busRowAddressB.setValue(true);
                    }
                    if ((row & 1) == 1) {
                        busRowAddressA.setValue(true);
                    }

                    busOutputEnabled.setValue(false);
                    busSerialClock.setValue(false);
                    busDataLatch.setValue(false);

                    busLedR1.setValue(false);
                    busLedG1.setValue(false);
                    busLedB1.setValue(false);
                    busLedR2.setValue(false);
                    busLedG2.setValue(false);
                    busLedB2.setValue(false);

                    // strobe - on and off
                    busDataLatch.setValue(true);
                    busDataLatch.setValue(false);

                    // Now switch on for the given sleep time.
                    busOutputEnabled.setValue(true);

                    // If we use less bits, then use the upper areas which leaves us more CPU time to do other stuff.
                    SystemClock.sleep(TimeUnit.NANOSECONDS.toMillis(RGBmatrixPanel.RowSleepNanos[b]));
                    Log.d("TUT", "drawing something?");
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            bus.setBaudrate(31250);
            bus.setDataSize(8);
            bus.setParity(UartDevice.PARITY_NONE);
            bus.setStopBits(1);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot configure peripheral", e);
        }

        try {
            bus.registerUartDeviceCallback(callback);
            Log.d("!!!", "Awaiting data");
        } catch (IOException e) {
            throw new IllegalStateException("Cannot register for data from peripheral", e);
        }

        midiManager.registerDeviceCallback(deviceCallback, handler);

//        handler.post(mockInput);
    }

    private final MidiManager.DeviceCallback deviceCallback = new MidiManager.DeviceCallback() {
        @Override
        public void onDeviceAdded(MidiDeviceInfo device) {
            Bundle properties = device.getProperties();
            String name = properties.getString(MidiDeviceInfo.PROPERTY_NAME);
            Log.d("TUT", "onDeviceAdded: " + name);
            midiManager.openDevice(
                device,
                new MidiManager.OnDeviceOpenedListener() {
                    @Override
                    public void onDeviceOpened(MidiDevice device) {
                        MidiOutputPort port = device.openOutputPort(0);
                        port.onConnect(new MidiReceiver() {
                            @Override
                            public void onSend(byte[] msg, int offset, int count, long timestamp) throws IOException {
                                Log.d("TUT", "wut" + msg);
                            }
                        });
                    }
                },
                handler
            );
        }
    };

    private final UartDeviceCallback callback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
//            try {
//                Log.d("!!!", "got data!");
//                final int maxCount = 16;
//                byte[] buffer = new byte[maxCount];
//
//                int count;
//                while ((count = uart.read(buffer, buffer.length)) > 0) {
//                    Log.d("!!!", "Read " + count + " bytes from peripheral");
//                }
//                for (byte b : buffer) {
//                    Log.d("!!!", "byte " + b);
//                }
//                Log.d("!!!", "wtf string " + Arrays.asList(new String(buffer)));
//                Log.d("!!!", "wtf array " + Arrays.asList(buffer));
//                Log.d("!!!", "wtf raw " + buffer);
//
//            } catch (IOException e) {
//                throw new IllegalStateException("Fubar", e);
//            }
            return true;
        }
    };

    private final Runnable mockInput = new Runnable() {
        @Override
        public void run() {
            Log.d("!!!", "X Fake input");

//            presenter.onNotesPlayed(new Note(new Random().nextInt(127)));
            final int maxCount = 16;
            byte[] buffer = new byte[maxCount];
            int count;
            try {
                while ((count = bus.read(buffer, buffer.length)) > 0) {
                    Log.d("!!!", "X Read " + count + " bytes from peripheral");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (byte b : buffer) {
                Log.d("!!!", "byte " + b);
            }
            Log.d("!!!", "wtf " + Arrays.asList(buffer));

            handler.postDelayed(mockInput, TimeUnit.SECONDS.toMillis(3));
        }
    };

    @Override
    public void showRound(RoundViewModel viewModel) {
        Log.d("!!!", viewModel.getStatusMessage());
        for (String notation : viewModel) {
            Log.d("!!!", "Note: " + notation);
        }
    }

    @Override
    public void showGameComplete(GameOverViewModel viewModel) {
        Log.d("!!!", viewModel.getMessage());
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregisterUartDeviceCallback(callback);
        midiManager.unregisterDeviceCallback(deviceCallback);
//        handler.removeCallbacks(mockInput);
    }

    @Override
    protected void onDestroy() {
        try {
            bus.close();
        } catch (IOException e) {
            Log.e("!!!", "Cannot close peripheral bus, might encounter strange behaviour on next app start.");
        }
        super.onDestroy();
    }
}
