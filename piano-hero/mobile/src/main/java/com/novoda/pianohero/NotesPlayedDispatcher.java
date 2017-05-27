package com.novoda.pianohero;

import java.util.HashSet;
import java.util.Set;

class NotesPlayedDispatcher implements NoteListener {

    private final Set<Note> notesOn = new HashSet<>();
    private final GamePresenter presenter;

    NotesPlayedDispatcher(GamePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onPress(Note note) {
        notesOn.add(note);
        dispatchNotesPlayed();
    }

    @Override
    public void onRelease(Note note) {
        notesOn.remove(note);
        dispatchNotesPlayed();
    }

    private void dispatchNotesPlayed() {
        presenter.onNotesPlayed(notesOn.toArray(new Note[notesOn.size()]));
    }

}
