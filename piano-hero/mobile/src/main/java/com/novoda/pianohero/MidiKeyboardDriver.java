package com.novoda.pianohero;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Log;

import jp.kshoji.driver.midi.device.MidiInputDevice;

interface MidiKeyboardDriver {

    void attachListener(KeyListener keyListener);

    void attachListener(NoteListener noteListener);

    void open();

    void close();

    interface KeyListener {

        void onKeyPressed(Note note);

    }

    class KeyStationMini32 extends SimpleUsbMidiDriver implements MidiKeyboardDriver {
        private KeyListener keyListener;
        private NoteListener noteListener;

        KeyStationMini32(Context context) {
            super(context);
        }

        @Override
        public void attachListener(KeyListener keyListener) {
            this.keyListener = keyListener;
        }

        @Override
        public void attachListener(NoteListener noteListener) {
            this.noteListener = noteListener;
        }

        @Override
        public void onDeviceAttached(UsbDevice usbDevice) {
            Log.d("!!!", "device attached");
        }

        @Override
        public void onDeviceDetached(UsbDevice usbDevice) {
            Log.d("!!!", "device detached");
        }

        @Override
        public void onMidiNoteOn(
                MidiInputDevice midiInputDevice,
                int cable,
                int channel,
                int note,
                int velocity) {
            Note wrappedNote = new Note(note);
            if (noteListener != null) {
                noteListener.onPress(wrappedNote);
            }
            if (keyListener != null) {
                keyListener.onKeyPressed(wrappedNote);
            }
        }

        @Override
        public void onMidiNoteOff(
                MidiInputDevice midiInputDevice,
                int cable,
                int channel,
                int note,
                int velocity) {
            if (noteListener != null) {
                noteListener.onRelease(new Note(note));
            }
        }
    }
}
