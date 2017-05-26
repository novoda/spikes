package com.novoda.pianohero;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;
import jp.kshoji.driver.midi.util.UsbMidiDriver;

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

    class KeyStationMini32 extends UsbMidiDriver implements MidiKeyboardDriver {
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

        protected KeyStationMini32(@NonNull Context context) {
            super(context);
        }

        @Override
        public void onDeviceAttached(@NonNull UsbDevice usbDevice) {
            Log.d("!!!", "device attached");
        }

        @Override
        public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
            // not used
        }

        @Override
        public void onMidiOutputDeviceAttached(@NonNull MidiOutputDevice midiOutputDevice) {
            // not used
        }

        @Override
        public void onDeviceDetached(@NonNull UsbDevice usbDevice) {
            Log.d("!!!", "device detached");
        }

        @Override
        public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
            // not used
        }

        @Override
        public void onMidiOutputDeviceDetached(@NonNull MidiOutputDevice midiOutputDevice) {
            // not used
        }

        @Override
        public void onMidiMiscellaneousFunctionCodes(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
            // not used
        }

        @Override
        public void onMidiCableEvents(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
            // not used
        }

        @Override
        public void onMidiSystemCommonMessage(@NonNull MidiInputDevice midiInputDevice, int i, byte[] bytes) {
            // not used
        }

        @Override
        public void onMidiSystemExclusive(@NonNull MidiInputDevice midiInputDevice, int i, byte[] bytes) {
            // not used
        }

        @Override
        public void attachListener(KeyListener keyListener) {
            this.keyListener = keyListener;
        }

        /**
         * Note-on
         * Code Index Number : 0x9
         *
         * @param midiInputDevice the Object which the event sent
         * @param cable           the cable ID 0-15
         * @param channel         the MIDI channel number 0-15
         * @param note            0-127
         * @param velocity        0-127
         */
        @Override
        public void onMidiNoteOn(
                @NonNull MidiInputDevice midiInputDevice,
                int cable,
                int channel,
                int note,
                int velocity) {
            // not used
        }

        /**
         * Note-off
         * Code Index Number : 0x8
         *
         * @param midiInputDevice the Object which the event sent
         * @param cable           the cable ID 0-15
         * @param channel         0-15
         * @param note            0-127
         * @param velocity        0-127
         */
        @Override
        public void onMidiNoteOff(
                @NonNull MidiInputDevice midiInputDevice,
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

        @Override
        public void onMidiPolyphonicAftertouch(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
            // not used
        }

        @Override
        public void onMidiControlChange(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
            // not used
        }

        @Override
        public void onMidiProgramChange(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2) {
            // not used
        }

        @Override
        public void onMidiChannelAftertouch(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2) {
            // not used
        }

        @Override
        public void onMidiPitchWheel(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2) {
            // not used
        }

        @Override
        public void onMidiSingleByte(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {
            // not used
        }

        @Override
        public void onMidiTimeCodeQuarterFrame(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {
            // not used
        }

        @Override
        public void onMidiSongSelect(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {
            // not used
        }

        @Override
        public void onMidiSongPositionPointer(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {
            // not used
        }

        @Override
        public void onMidiTuneRequest(@NonNull MidiInputDevice midiInputDevice, int i) {
            // not used
        }

        @Override
        public void onMidiTimingClock(@NonNull MidiInputDevice midiInputDevice, int i) {
            // not used
        }

        @Override
        public void onMidiStart(@NonNull MidiInputDevice midiInputDevice, int i) {
            // not used
        }

        @Override
        public void onMidiContinue(@NonNull MidiInputDevice midiInputDevice, int i) {
            // not used
        }

        @Override
        public void onMidiStop(@NonNull MidiInputDevice midiInputDevice, int i) {
            // not used
        }

        @Override
        public void onMidiActiveSensing(@NonNull MidiInputDevice midiInputDevice, int i) {
            // not used
        }

        @Override
        public void onMidiReset(@NonNull MidiInputDevice midiInputDevice, int i) {
            // not used
        }
    }
}
