package com.novoda.pianohero;

public class SongSequenceFactory {

    public Sequence maryHadALittleLamb() { // ???
        return new Sequence.Builder()
                .add(Note.E4).add(Note.D4).add(Note.C4).add(Note.D4).add(Note.E4).add(Note.E4).add(Note.E4)
                .add(Note.D4).add(Note.D4).add(Note.D4).add(Note.E4).add(Note.E4).add(Note.E4)
                .add(Note.E4).add(Note.D4).add(Note.C4).add(Note.D4).add(Note.E4).add(Note.E4).add(Note.E4)
                .add(Note.E4).add(Note.D4).add(Note.D4).add(Note.E4).add(Note.D4).add(Note.C4)
                .build();
    }

    Sequence onceUponADream() { // sleeping beauty
        return new Sequence.Builder()
                .add(Note.G4).add(Note.F4_S).add(Note.G4).add(Note.E4).add(Note.F4_S).add(Note.G4).add(Note.E4)
                .add(Note.F4_S).add(Note.A4).add(Note.B4).add(Note.G4_S).add(Note.A4)
                .build();
    }

    Sequence whenSheLovedMe() { // toy story
        return new Sequence.Builder()
                .add(Note.D4).add(Note.G4).add(Note.G4).add(Note.F4_S).add(Note.G4).add(Note.D4)
                .add(Note.E4).add(Note.G4).add(Note.G4).add(Note.B4).add(Note.A4).add(Note.G4).add(Note.A4)
                .add(Note.F4_S).add(Note.A4).add(Note.B4).add(Note.A4).add(Note.G4).add(Note.A4).add(Note.B4).add(Note.D5)
                .add(Note.E4).add(Note.D4).add(Note.E4).add(Note.G4).add(Note.A4)
                .build();
    }

    Sequence aWholeNewWorld() { // aladdin
        return new Sequence.Builder()
                .add(Note.G4_S).add(Note.F4_S).add(Note.A4).add(Note.G4_S).add(Note.E4).add(Note.B4)
                .add(Note.G4_S).add(Note.F4_S).add(Note.A4).add(Note.G4_S).add(Note.E4).add(Note.G4_S).add(Note.F4_S)
                .add(Note.F4_S).add(Note.E4).add(Note.G4_S).add(Note.F4_S).add(Note.D4_S).add(Note.F4_S).add(Note.E4).add(Note.D4).add(Note.E4)
                .add(Note.C4_S).add(Note.D4_S).add(Note.F4_S).add(Note.E4).add(Note.G4_S)
                .build();
    }

    Sequence theBareNecessities() { // jungle book
        return new Sequence.Builder()
                .add(Note.D4).add(Note.E4).add(Note.G4)
                .add(Note.B4).add(Note.A4_S).add(Note.B4).add(Note.A4).add(Note.G4)
                .add(Note.G4).add(Note.A4).add(Note.G4).add(Note.A4).add(Note.G4).add(Note.A4).add(Note.G4).add(Note.E4)
                .build();
    }

    Sequence beautyAndTheBeast() { // b&tb
        return new Sequence.Builder()
                .add(Note.D4).add(Note.F4).add(Note.A4).add(Note.A4_S).add(Note.D4_S)
                .add(Note.D4).add(Note.F4).add(Note.A4).add(Note.A4_S).add(Note.C5)
                .add(Note.A4_S).add(Note.C5).add(Note.D5).add(Note.D5_S).add(Note.F5)
                .add(Note.F5).add(Note.D5_S).add(Note.D5).add(Note.C5).add(Note.A4_S)
                .add(Note.D5_S).add(Note.D5).add(Note.C5).add(Note.A4_S).add(Note.F4)
                .build();
    }
}
