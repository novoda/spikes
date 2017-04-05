package com.novoda.pianohero;

import java.util.ArrayList;
import java.util.List;

public final class Sequence {

    private final List<Notes> notes;
    private final int position;
    private final Notes latestError;

    private Sequence(List<Notes> notes, int position, Notes latestError) {
        this.notes = notes;
        this.position = position;
        this.latestError = latestError;
    }

    public Notes get(int position) {
        return notes().get(position);
    }

    public int position() {
        return position;
    }

    public Notes latestError() {
        return latestError;
    }

    public List<Notes> notes() {
        return notes;
    }

    public int length() {
        return notes().size();
    }

    public static class Builder {

        private final List<Notes> notes = new ArrayList<>();

        private Notes latestError = Notes.EMPTY;
        private int position = 0;

        public Builder() {
            this(new ArrayList<Notes>());
        }

        public Builder(Sequence sequence) {
            this(sequence.notes());
            this.latestError = sequence.latestError();
            this.position = sequence.position;
        }

        private Builder(List<Notes> notes) {
            this.notes.addAll(notes);
        }

        public Builder add(Chord chord) {
            notes.add(chord);
            return this;
        }

        public Builder add(Note note) {
            notes.add(new Notes(note));
            return this;
        }

        public Builder withLatestError(Notes latestError) {
            this.latestError = latestError;
            return this;
        }

        public Builder atPosition(int position) {
            this.position = position;
            return this;
        }

        public Sequence build() {
            return new Sequence(notes, position, latestError);
        }
    }
}
