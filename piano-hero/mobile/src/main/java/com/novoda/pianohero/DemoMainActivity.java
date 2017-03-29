package com.novoda.pianohero;

import android.app.Activity;
import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiManager.DeviceCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DemoMainActivity extends Activity {

    private static final String TAG = "!!!";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MidiManager midiManager = (MidiManager) getSystemService(Context.MIDI_SERVICE);
        final Handler handler = new Handler(Looper.getMainLooper());

        midiManager.registerDeviceCallback(new DeviceCallback() {
            @Override
            public void onDeviceAdded(MidiDeviceInfo device) {
                super.onDeviceAdded(device);
                Log.d(TAG, "onDeviceAdded: " + device.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME));
                midiManager.openDevice(device, new MidiManager.OnDeviceOpenedListener() {
                    @Override
                    public void onDeviceOpened(MidiDevice device) {
//                        MidiOutputPort midiOutputPort = device.openOutputPort(0);
//                        midiOutputPort.onConnect(new MidiReceiver() {
//                            @Override
//                            public void onSend(byte[] msg, int offset, int count, long timestamp) throws IOException {
//                                // we get lots of events here even if no key presses
//                                // parse the relevant ones (note-on & note-off). check out - https://github.com/google/music-synthesizer-for-android
//                            }
//                        });
                    }
                }, handler);
            }

            @Override
            public void onDeviceRemoved(MidiDeviceInfo device) {
                super.onDeviceRemoved(device);
                Log.d(TAG, "onDeviceRemoved: " + device.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME));
            }
        }, handler);
    }

}
