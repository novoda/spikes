package com.novoda.pianohero;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Notes {

    private final Set<Note> notes;

    public Notes(Note... notes) {
        this(new HashSet<>(Arrays.asList(notes)));
    }

    public Notes(Set<Note> notes) {
        this.notes = notes;
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

        Notes notes = (Notes) o;
        return notes.equals(notes.notes);
    }

    @Override
    public int hashCode() {
        return notes.hashCode();
    }

    @Override
    public String toString() {
        return "Notes{" + notes + '}';
    }
}
