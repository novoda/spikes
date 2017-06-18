package com.novoda.pianohero;

class State {

    private final Score score;
    private final Sequence sequence;
    private final Sound sound;
    private final Message message;
    private final long secondsRemaining;

    static State empty() {
        return new State(Score.initial(), new Sequence.Builder().build(), Sound.ofSilence(), Message.empty(), 0);
    }

    State(Score score, Sequence sequence, Sound sound, Message message, long secondsRemaining) {
        this.score = score;
        this.sequence = sequence;
        this.sound = sound;
        this.message = message;
        this.secondsRemaining = secondsRemaining;
    }

    Score getScore() {
        return score;
    }

    Sequence getSequence() {
        return sequence;
    }

    Sound getSound() {
        return sound;
    }

    Message getMessage() {
        return message;
    }

    long getSecondsRemaining() {
        return secondsRemaining;
    }

    State update(Score score) {
        return new State(score, getSequence(), getSound(), getMessage(), getSecondsRemaining());
    }

    State update(Sequence sequence) {
        return new State(getScore(), sequence, getSound(), getMessage(), getSecondsRemaining());
    }

    State update(Sound sound) {
        return new State(getScore(), getSequence(), sound, getMessage(), getSecondsRemaining());
    }

    State update(Message message) {
        return new State(getScore(), getSequence(), getSound(), message, getSecondsRemaining());
    }

    State update(long secondsRemaining) {
        return new State(getScore(), getSequence(), getSound(), getMessage(), secondsRemaining);
    }
}
