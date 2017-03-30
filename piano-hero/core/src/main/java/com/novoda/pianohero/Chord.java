package com.novoda.pianohero;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Chord {

    private final Set<Note> notes;

    public Chord(Note... notes) {
        this(new HashSet<>(Arrays.asList(notes)));
    }

    public Chord(Set<Note> notes) {
        this.notes = checkHasMinimumNotes(notes);
    }

    private Set<Note> checkHasMinimumNotes(Set<Note> notes) {
        if (notes.size() < 2) {
            throw new IllegalArgumentException("Chords must contain at least two Notes");
        }
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
