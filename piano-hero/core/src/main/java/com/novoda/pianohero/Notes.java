package com.novoda.pianohero;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Notes implements Iterable<Note> {

    public static final Notes EMPTY = new Notes(Collections.<Note>emptySet());

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

    public int count() {
        return notes.size();
    }

    @Override
    public Iterator<Note> iterator() {
        return notes.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return notes.equals(((Notes) o).notes);
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
