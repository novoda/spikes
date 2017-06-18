package com.novoda.pianohero;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SongSequencePlaylist {

    private static final List<Sequence> SEQUENCES = Arrays.asList(
            maryHadALittleLamb(),
            onceUponADream(),
            whenSheLovedMe(),
            aWholeNewWorld(),
            theBareNecessities(),
            beautyAndTheBeast()
    );

    private Iterator<Sequence> iterator = SEQUENCES.iterator();

    public Sequence nextSong() {
        if (!iterator.hasNext()) {
            iterator = SEQUENCES.iterator();
        }
        return iterator.next();
    }

    private static Sequence maryHadALittleLamb() {
        return new Sequence.Builder()
                .withTitle("Mary Had A Little Lamb")
                .add(Note.E4).add(Note.D4).add(Note.C4).add(Note.D4).add(Note.E4).add(Note.E4).add(Note.E4)
                .add(Note.D4).add(Note.D4).add(Note.D4).add(Note.E4).add(Note.E4).add(Note.E4)
                .add(Note.E4).add(Note.D4).add(Note.C4).add(Note.D4).add(Note.E4).add(Note.E4).add(Note.E4)
                .add(Note.E4).add(Note.D4).add(Note.D4).add(Note.E4).add(Note.D4).add(Note.C4)
                .build();
    }

    private static Sequence theBareNecessities() {
        return new Sequence.Builder()
                .withTitle("The Jungle Book - The Bare Necessities")
                .add(Note.D4).add(Note.E4).add(Note.G4)
                .add(Note.B4).add(Note.A4_S).add(Note.B4).add(Note.A4).add(Note.G4)
                .add(Note.G4).add(Note.A4).add(Note.G4).add(Note.A4).add(Note.G4).add(Note.A4).add(Note.G4).add(Note.E4)
                .build();
    }

    private static Sequence onceUponADream() {
        return new Sequence.Builder()
                .withTitle("Sleeping Beauty - Once Upon A Dream")
                .add(Note.G4).add(Note.F4_S).add(Note.G4).add(Note.E4).add(Note.F4_S).add(Note.G4).add(Note.E4)
                .add(Note.F4_S).add(Note.A4).add(Note.B4).add(Note.G4_S).add(Note.A4)
                .build();
    }

    private static Sequence whenSheLovedMe() {
        return new Sequence.Builder()
                .withTitle("Toy Story - When She Loved Me")
                .add(Note.D4).add(Note.G4).add(Note.G4).add(Note.F4_S).add(Note.G4).add(Note.D4)
                .add(Note.E4).add(Note.G4).add(Note.G4).add(Note.B4).add(Note.A4).add(Note.G4).add(Note.A4)
                .add(Note.F4_S).add(Note.A4).add(Note.B4).add(Note.A4).add(Note.G4).add(Note.A4).add(Note.B4).add(Note.D5)
                .add(Note.E4).add(Note.D4).add(Note.E4).add(Note.G4).add(Note.A4)
                .build();
    }

    private static Sequence aWholeNewWorld() {
        return new Sequence.Builder()
                .withTitle("Aladdin - A Whole New World")
                .add(Note.G4_S).add(Note.F4_S).add(Note.A4).add(Note.G4_S).add(Note.E4).add(Note.B4)
                .add(Note.G4_S).add(Note.F4_S).add(Note.A4).add(Note.G4_S).add(Note.E4).add(Note.G4_S).add(Note.F4_S)
                .add(Note.F4_S).add(Note.E4).add(Note.G4_S).add(Note.F4_S).add(Note.D4_S).add(Note.F4_S).add(Note.E4).add(Note.D4).add(Note.E4)
                .add(Note.C4_S).add(Note.D4_S).add(Note.F4_S).add(Note.E4).add(Note.G4_S)
                .build();
    }

    private static Sequence beautyAndTheBeast() {
        return new Sequence.Builder()
                .withTitle("Beauty And The Beast")
                .add(Note.D4).add(Note.F4).add(Note.A4).add(Note.A4_S).add(Note.D4_S)
                .add(Note.D4).add(Note.F4).add(Note.A4).add(Note.A4_S).add(Note.C5)
                .add(Note.A4_S).add(Note.C5).add(Note.D5).add(Note.D5_S).add(Note.F5)
                .add(Note.F5).add(Note.D5_S).add(Note.D5).add(Note.C5).add(Note.A4_S)
                .add(Note.D5_S).add(Note.D5).add(Note.C5).add(Note.A4_S).add(Note.F4)
                .build();
    }
}
