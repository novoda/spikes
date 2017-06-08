package com.novoda.pianohero;

import java.util.Iterator;
import java.util.List;

public class Notes implements Iterable<Note> {

    private final List<Note> notes;

    public Notes(List<Note> notes) {
        this.notes = notes;
    }

    public List<Note> asList() {
        return notes;
    }

    public int length() {
        return notes.size();
    }

    @Override
    public Iterator<Note> iterator() {
        return notes.iterator();
    }

    public Note get(int position) {
        return notes.get(position);
    }

    public boolean hasNoteAt(int position) {
        return position < notes.size();
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
