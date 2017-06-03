package com.novoda.pianohero;

import java.util.Arrays;
import java.util.List;

class CompositePiano implements Piano {

    private final List<Piano> pianos;

    CompositePiano(Piano... pianos) {
        this.pianos = Arrays.asList(pianos);
    }

    @Override
    public void attachListener(NoteListener noteListener) {
        for (Piano piano : pianos) {
            piano.attachListener(noteListener);
        }
    }

    @Override
    public void open() {
        for (Piano piano : pianos) {
            piano.open();
        }
    }

    @Override
    public void close() {
        for (Piano piano : pianos) {
            piano.close();
        }
    }

}
