package com.novoda.pianohero;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Log;
import android.util.SparseArray;

import jp.kshoji.driver.midi.device.MidiInputDevice;

interface MidiKeyboardDriver {

    void attachListener(KeyListener keyListener);

    void open();

    void close();

    interface KeyListener {

        void onKeyPressed(Note note);

    }

    enum Note {
        C3,
        D3,
        E3,
        F3,
        G3,
        A3,
        B3,
        C4,
        D4,
        E4,
        F4,
        G4,
        A4,
        B4,
        C5,
        D5,
        E5,
        F5,
        G5,
        NOT_IMPLED
    }

    class KeyStationMini32 extends SimpleUsbMidiDriver implements MidiKeyboardDriver {
        private KeyListener keyListener;

        private static final SparseArray<Note> NOTES_MAPPING = new SparseArray<>();

        static {
            NOTES_MAPPING.put(48, Note.C3);
            NOTES_MAPPING.put(50, Note.D3);
            NOTES_MAPPING.put(52, Note.E3);
            NOTES_MAPPING.put(53, Note.F3);
            NOTES_MAPPING.put(55, Note.G3);
            NOTES_MAPPING.put(57, Note.A3);
            NOTES_MAPPING.put(59, Note.B3);
            NOTES_MAPPING.put(60, Note.C4);
            NOTES_MAPPING.put(62, Note.D4);
            NOTES_MAPPING.put(64, Note.E4);
            NOTES_MAPPING.put(65, Note.F4);
            NOTES_MAPPING.put(67, Note.G4);
            NOTES_MAPPING.put(69, Note.A4);
            NOTES_MAPPING.put(71, Note.B4);
            NOTES_MAPPING.put(72, Note.C5);
            NOTES_MAPPING.put(74, Note.D5);
            NOTES_MAPPING.put(76, Note.E5);
            NOTES_MAPPING.put(77, Note.F5);
            NOTES_MAPPING.put(79, Note.G5);
        }

        KeyStationMini32(Context context) {
            super(context);
        }

        @Override
        public void attachListener(KeyListener keyListener) {
            this.keyListener = keyListener;
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
        public void onMidiNoteOff(
                MidiInputDevice midiInputDevice,
                int cable,
                int channel,
                int note,
                int velocity) {
            if (keyListener == null) {
                return;
            }
            if (NOTES_MAPPING.indexOfKey(note) < 0) {
                keyListener.onKeyPressed(Note.NOT_IMPLED);
            } else {
                keyListener.onKeyPressed(NOTES_MAPPING.get(note));
            }
        }
    }
}
