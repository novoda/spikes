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

    public Sequence pachelbelsCanon() {
        return new Sequence.Builder()
                .add(Chords.C_2ND_INV).add(Chords.G).add(Chords.AM_2ND_INV).add(Chords.EM)
                .add(Chords.F_2ND_INV).add(Chords.C).add(Chords.F_2ND_INV).add(Chords.G_2ND_INV)
                .add(Chords.C_2ND_INV).add(Chords.G).add(Chords.AM_2ND_INV).add(Chords.EM)
                .add(Chords.F_2ND_INV).add(Chords.C).add(Chords.F_2ND_INV).add(Chords.G_2ND_INV)
                .add(Chords.C_2ND_INV).add(Chords.G).add(Chords.AM_2ND_INV).add(Chords.EM)
                .add(Chords.F_2ND_INV).add(Chords.C).add(Chords.F_2ND_INV).add(Chords.G_2ND_INV).add(Chords.C)
                .build();
    }
}
