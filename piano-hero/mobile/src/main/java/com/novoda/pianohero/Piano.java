package com.novoda.pianohero;

interface Piano {

    void attachListener(NoteListener noteListener);

    interface NoteListener {

        void onStart(Note note);

        void onStop(Note note);
    }
}
