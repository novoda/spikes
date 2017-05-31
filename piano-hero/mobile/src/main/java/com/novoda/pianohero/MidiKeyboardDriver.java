package com.novoda.pianohero;

import android.content.Context;

import jp.kshoji.driver.midi.device.MidiInputDevice;

interface MidiKeyboardDriver {

    void attachListener(NoteListener noteListener);

    void open();

    void close();

    class KeyStationMini32 extends SimpleUsbMidiDriver implements MidiKeyboardDriver {
        private NoteListener noteListener;

        KeyStationMini32(Context context) {
            super(context);
        }

        @Override
        public void attachListener(NoteListener noteListener) {
            this.noteListener = noteListener;
        }

        @Override
        public void onMidiNoteOff(
                MidiInputDevice midiInputDevice,
                int cable,
                int channel,
                int note,
                int velocity) {
            if (noteListener != null) {
                noteListener.onPlay(new Note(note));
            }
        }
    }
}
