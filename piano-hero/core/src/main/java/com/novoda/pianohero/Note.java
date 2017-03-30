package com.novoda.pianohero;

public class Note {

    private final int midiNoteNumber;

    public Note(int midiNoteNumber) {
        this.midiNoteNumber = checkNotOutOfRange(midiNoteNumber);
    }

    private int checkNotOutOfRange(int midiNoteNumber) {
        if (midiNoteNumber < 0 && midiNoteNumber > 127) {
            throw new IllegalArgumentException("MIDI notes range from 0-127 inclusive");
        }
        return midiNoteNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Note note = (Note) o;
        return midiNoteNumber == note.midiNoteNumber;
    }

    @Override
    public int hashCode() {
        return midiNoteNumber;
    }
}
