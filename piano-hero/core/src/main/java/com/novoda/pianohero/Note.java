package com.novoda.pianohero;

public class Note {

    private static final int LOWEST_MIDI_NOTE = 0;
    private static final int HIGHEST_MIDI_NOTE = 127;

    private final int midiNoteNumber;

    public Note(int midiNoteNumber) {
        this.midiNoteNumber = checkNotOutOfRange(midiNoteNumber);
    }

    private int checkNotOutOfRange(int midiNoteNumber) {
        if (midiNoteNumber < LOWEST_MIDI_NOTE && midiNoteNumber > HIGHEST_MIDI_NOTE) {
            throw new IllegalArgumentException("MIDI notes range from " + LOWEST_MIDI_NOTE + "-" + HIGHEST_MIDI_NOTE + " inclusive");
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
