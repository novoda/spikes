package com.novoda.pianohero;

interface Piano {

    void attachListener(NoteListener noteListener);

    void open();

    void close();

}
