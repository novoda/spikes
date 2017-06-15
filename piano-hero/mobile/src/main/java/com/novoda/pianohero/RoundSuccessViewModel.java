package com.novoda.pianohero;

class RoundSuccessViewModel {
    private final int score;

    public RoundSuccessViewModel(int score) {
        this.score = score;
    }

    public String getScoreFormatted() {
        return String.valueOf(score);
    }
}
