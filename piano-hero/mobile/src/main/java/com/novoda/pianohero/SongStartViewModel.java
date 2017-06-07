package com.novoda.pianohero;

class SongStartViewModel {

    private final String currentNoteFormatted;
    private final String nextNoteFormatted;

    SongStartViewModel(String currentNoteFormatted, String nextNoteFormatted) {
        this.currentNoteFormatted = currentNoteFormatted;
        this.nextNoteFormatted = nextNoteFormatted;
    }

    public String getCurrentNoteFormatted() {
        return currentNoteFormatted;
    }

    public String getNextNoteFormatted() {
        return nextNoteFormatted;
    }

}
