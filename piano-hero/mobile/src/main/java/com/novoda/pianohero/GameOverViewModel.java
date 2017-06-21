package com.novoda.pianohero;

public class GameOverViewModel {

    private final CharSequence message;

    public GameOverViewModel(CharSequence message) {
        this.message = message;
    }

    public CharSequence getMessage() {
        return message;
    }
}
