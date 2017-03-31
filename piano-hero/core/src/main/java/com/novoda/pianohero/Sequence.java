package com.novoda.pianohero;

import java.util.ArrayList;
import java.util.List;

public final class Sequence {

    private final List<Notes> notes;

    private Sequence(List<Notes> notes) {
        this.notes = notes;
    }

    public Notes get(int position) {
        return notes.get(position);
    }

    public int length() {
        return notes.size();
    }

    public static class Builder {

        private final List<Notes> notes = new ArrayList<>();

        public Builder add(Chord chord) {
            notes.add(chord);
            return this;
        }

        public Builder add(Note note) {
            notes.add(new Notes(note));
            return this;
        }

        public Sequence build() {
            return new Sequence(notes);
        }
    }
}
