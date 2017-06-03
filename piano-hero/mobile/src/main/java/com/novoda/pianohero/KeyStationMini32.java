package com.novoda.pianohero;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import jp.kshoji.driver.midi.device.MidiInputDevice;

class KeyStationMini32 extends SimpleUsbMidiDriver implements Piano {

    private NoteListener noteListener;
    private Handler handler;

    KeyStationMini32(Context context) {
        super(context);
        handler = new Handler(Looper.getMainLooper());
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
            final int note,
            int velocity) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (noteListener != null) {
                    noteListener.onPlay(new Note(note));
                }
            }
        });
    }

}
