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
                .add(Chord.C_2ND_INV).add(Chord.G).add(Chord.AM_2ND_INV).add(Chord.EM)
                .add(Chord.F_2ND_INV).add(Chord.C).add(Chord.F_2ND_INV).add(Chord.G_2ND_INV)
                .add(Chord.C_2ND_INV).add(Chord.G).add(Chord.AM_2ND_INV).add(Chord.EM)
                .add(Chord.F_2ND_INV).add(Chord.C).add(Chord.F_2ND_INV).add(Chord.G_2ND_INV)
                .add(Chord.C_2ND_INV).add(Chord.G).add(Chord.AM_2ND_INV).add(Chord.EM)
                .add(Chord.F_2ND_INV).add(Chord.C).add(Chord.F_2ND_INV).add(Chord.G_2ND_INV).add(Chord.C)
                .build();
    }
}
