package com.novoda.pianohero;

class State {

    private final Score score;
    private final Sequence sequence;
    private final Sound sound;
    private final Message message;
    private final long millisRemaining;

    static State empty() {
        return new State(Score.initial(), new Sequence.Builder().build(), Sound.ofSilence(), Message.empty(), 0);
    }

    static State initial(Sequence sequence) {
        return new State(Score.initial(), sequence, Sound.ofSilence(), Message.empty(), 0);
    }

    State(Score score, Sequence sequence, Sound sound, Message message, long millisRemaining) {
        this.score = score;
        this.sequence = sequence;
        this.sound = sound;
        this.message = message;
        this.millisRemaining = millisRemaining;
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

    long getMillisRemaining() {
        return millisRemaining;
    }

    State update(Score score) {
        return new State(score, getSequence(), getSound(), getMessage(), getMillisRemaining());
    }

    State update(Sequence sequence) {
        return new State(getScore(), sequence, getSound(), getMessage(), getMillisRemaining());
    }

    State update(Sound sound) {
        return new State(getScore(), getSequence(), sound, getMessage(), getMillisRemaining());
    }

    State update(Message message) {
        return new State(getScore(), getSequence(), getSound(), message, getMillisRemaining());
    }

    State update(long millisRemaining) {
        return new State(getScore(), getSequence(), getSound(), getMessage(), millisRemaining);
    }
}
