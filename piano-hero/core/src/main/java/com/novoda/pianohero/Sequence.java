package com.novoda.pianohero;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public final class Sequence {

    private final Notes notes;
    private final int position;
    @Nullable
    private final Note latestError;

    private Sequence(Notes notes, int position, @Nullable Note latestError) {
        this.notes = notes;
        this.position = position;
        this.latestError = latestError;
    }

    public Note get(int position) {
        return notes().get(position);
    }

    public int position() {
        return position;
    }

    @Nullable
    public Note latestError() {
        return latestError;
    }

    public Notes notes() {
        return notes;
    }

    public int length() {
        return notes().length();
    }

    public static class Builder {

        private final List<Note> notes = new ArrayList<>();

        @Nullable
        private Note latestError;

        private int position = 0;

        public Builder() {
            this(new ArrayList<Note>());
        }

        public Builder(Sequence sequence) {
            this(sequence.notes().notes());
            this.latestError = sequence.latestError();
            this.position = sequence.position;
        }

        private Builder(List<Note> notes) {
            this.notes.addAll(notes);
        }

        public Builder add(Note note) {
            notes.add(note);
            return this;
        }

        public Builder withLatestError(@Nullable Note latestError) {
            this.latestError = latestError;
            return this;
        }

        public Builder atPosition(int position) {
            this.position = position;
            return this;
        }

        public Sequence build() {
            return new Sequence(new Notes(notes), position, latestError);
        }
    }
}
