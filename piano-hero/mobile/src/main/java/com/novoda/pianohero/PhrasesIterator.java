package com.novoda.pianohero;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

class PhrasesIterator {

    private static final List<CharSequence> PRAISE = Arrays.<CharSequence>asList(
            "Fantastic, keep going!",
            "Reach for the stars!",
            "You're awesome!",
            "¡Buen trabajo!",
            "Brilliant!",
            "Just do it!",
            "There you go!",
            "It brings a tear to my eye...",
            "Come on!",
            "Never give up!",
            "Follow your dreams!",
            "Great job!",
            "Believe in yourself..!",
            "Look how far you've come!",
            "That's what we call perseverance!",
            "I like the way you handled that!",
            "Such coordination!",
            "Have you had lessons?",
            "Excellent!",
            "You chose wisely..!",
            "You make it look easy!",
            "Outstanding!",
            "I'm behind you 100%!",
            "You've got this!",
            "So, what's the name of your band?",
            "The sky is the limit!",
            "You've really got the hang of it!",
            "You're doing great!"
    );

    private static final List<String > GAME_OVER = Arrays.asList(
            "Brilliant, you scored %d!",
            "%d, nice! Want to try again?",
            "You got %d, not too shabby!",
            "Well done, you got %d"
    );

    private Iterator<CharSequence> praiseIterator = PRAISE.iterator();
    private Iterator<String> gameOverIterator = GAME_OVER.iterator();

    CharSequence nextPlatitude() {
        if (!praiseIterator.hasNext()) {
            praiseIterator = PRAISE.iterator();
        }
        return praiseIterator.next();
    }

    CharSequence nextGameOverMessage(int score) {
        if (!gameOverIterator.hasNext()) {
            gameOverIterator = GAME_OVER.iterator();
        }
        return String.format(Locale.US, gameOverIterator.next(), score);
    }
}
