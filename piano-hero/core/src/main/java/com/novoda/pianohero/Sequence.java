package com.novoda.pianohero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class Sequence {

    private final List<Set<Note>> notes;

    private Sequence(List<Set<Note>> notes) {
        this.notes = notes;
    }

    public Set<Note> get(int position) {
        return notes.get(position);
    }

    public int size() {
        return notes.size();
    }

    public static class Builder {

        private final List<Set<Note>> notes = new ArrayList<>();

        public Builder add(Chord chord) {
            notes.add(chord.notes());
            return this;
        }

        public Builder add(Note note) {
            notes.add(Collections.singleton(note));
            return this;
        }

        public Sequence build() {
            return new Sequence(notes);
        }
    }
}
