package com.novoda.pianohero;

import java.util.Arrays;
import java.util.List;

public class SimplePitchNotationFormatter {

    private static final int NOTES_IN_OCTAVE = 12;
    private static final List<String> ORDERED_NOTES = Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");

    public String format(Note note) {
        if(note == Note.NONE) {
            return "";
        }
        int i = note.midi() % NOTES_IN_OCTAVE;
        return ORDERED_NOTES.get(i);
    }
}
