package com.novoda.pianohero;

interface NoteListener {

    void onPress(Note note);

    void onRelease(Note note);
}
