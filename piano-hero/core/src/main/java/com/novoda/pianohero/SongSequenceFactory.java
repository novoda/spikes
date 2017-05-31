package com.novoda.pianohero;

public class SongSequenceFactory {

    public Sequence maryHadALittleLamb() {
        return new Sequence.Builder()
                .add(Note.E4).add(Note.D4).add(Note.C4).add(Note.D4).add(Note.E4).add(Note.E4).add(Note.E4)
                .add(Note.D4).add(Note.D4).add(Note.D4).add(Note.E4).add(Note.E4).add(Note.E4)
                .add(Note.E4).add(Note.D4).add(Note.C4).add(Note.D4).add(Note.E4).add(Note.E4).add(Note.E4)
                .add(Note.E4).add(Note.D4).add(Note.D4).add(Note.E4).add(Note.D4).add(Note.C4)
                .build();
    }
}
