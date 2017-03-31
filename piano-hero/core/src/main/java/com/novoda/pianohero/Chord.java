package com.novoda.pianohero;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Chord {

    static final Chord C = new Chord(Note.C4, Note.E4, Note.G4);
    static final Chord F_2ND_INV = new Chord(Note.C4, Note.F4, Note.A4);
    static final Chord G_2ND_INV = new Chord(Note.D4, Note.G4, Note.B4);
    static final Chord EM = new Chord(Note.E4, Note.G4, Note.B5);
    static final Chord AM_2ND_INV = new Chord(Note.E4, Note.A4, Note.C5);
    static final Chord G = new Chord(Note.G4, Note.B5, Note.D5);
    static final Chord C_2ND_INV = new Chord(Note.G4, Note.C5, Note.E5);

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
