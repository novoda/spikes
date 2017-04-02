package com.novoda.pianohero;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Chord {

    private static final int MINIMUM_NUMBER_OF_NOTES_IN_CHORD = 2;

    private final Set<Note> notes;

    public Chord(Note... notes) {
        this(new HashSet<>(Arrays.asList(notes)));
    }

    public Chord(Set<Note> notes) {
        this.notes = checkHasMinimumNotes(notes);
    }

    private Set<Note> checkHasMinimumNotes(Set<Note> notes) {
        if (notes.size() < MINIMUM_NUMBER_OF_NOTES_IN_CHORD) {
            throw new IllegalArgumentException("Chords must contain at least two Notes");
        }
        return notes;
    }

    public Set<Note> notes() {
        return notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Chord chord = (Chord) o;
        return notes.equals(chord.notes);
    }

    @Override
    public int hashCode() {
        return notes.hashCode();
    }
}
