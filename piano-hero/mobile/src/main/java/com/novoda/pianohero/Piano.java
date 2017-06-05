package com.novoda.pianohero;

interface Piano {

    void attachListener(NoteListener noteListener);

    void open();

    void close();

    interface NoteListener {

        void onStart(Note note);

        void onStop(Note note);
    }
}
