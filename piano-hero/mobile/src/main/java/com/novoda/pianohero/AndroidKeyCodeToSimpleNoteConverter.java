package com.novoda.pianohero;

import android.util.SparseArray;
import android.view.KeyEvent;

class AndroidKeyCodeToSimpleNoteConverter {

    private static final SparseArray<Note> SIMPLE_NOTES = new SparseArray<>();

    static {
        SIMPLE_NOTES.put(KeyEvent.KEYCODE_C, Note.C4);
        SIMPLE_NOTES.put(KeyEvent.KEYCODE_D, Note.D4);
        SIMPLE_NOTES.put(KeyEvent.KEYCODE_E, Note.E4);
        SIMPLE_NOTES.put(KeyEvent.KEYCODE_F, Note.F4);
        SIMPLE_NOTES.put(KeyEvent.KEYCODE_G, Note.G4);
        SIMPLE_NOTES.put(KeyEvent.KEYCODE_A, Note.A4);
        SIMPLE_NOTES.put(KeyEvent.KEYCODE_B, Note.B4);
    }

    public Note convert(int keyCode) {
        Note note = SIMPLE_NOTES.get(keyCode);
        if (note == null) {
            throw new IllegalArgumentException("Argument doesn't correspond to simple note: " + keyCode);
        } else {
            return note;
        }
    }
}
