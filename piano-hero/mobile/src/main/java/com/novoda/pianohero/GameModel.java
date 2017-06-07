package com.novoda.pianohero;

import android.os.CountDownTimer;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GameModel implements GameMvp.Model {

    private static final String NEXT_NOTE_HINT_FORMAT = "Next: %s";
    private static final String SHARP_SYMBOL = "#";
    private static final long GAME_LENGTH = TimeUnit.SECONDS.toMillis(60);
    private static final long CLOCK_UPDATE_RATE = TimeUnit.SECONDS.toMillis(1);
    private static final int START_SCORE = 500;

    private final SongSequenceFactory songSequenceFactory;
    private final SimplePitchNotationFormatter pitchNotationFormatter;
    private final Piano piano;

    private Sequence sequence;
    private int score = START_SCORE; // TODO object

    GameModel(
        SongSequenceFactory songSequenceFactory,
        SimplePitchNotationFormatter pitchNotationFormatter,
        Piano piano) {
        this.songSequenceFactory = songSequenceFactory;
        this.pitchNotationFormatter = pitchNotationFormatter;
        this.piano = piano;
    }

    @Override
    public void startGame(GameStartCallback callback,
                          final GameClockCallback clockCallback,
                          final RoundCallback roundCallback,
                          final SongCompleteCallback songCompleteCallback,
                          final GameCompleteCallback gameCompleteCallback) {
        score = START_SCORE;
        piano.attachListener(new Piano.NoteListener() {
            @Override
            public void onStart(Note note) {
                double frequency = frequencyFor(note);
                roundCallback.onRoundStart(new RoundStartViewModel(frequency));
            }

            @Override
            public void onStop(Note note) {
                playGameRound(roundCallback, songCompleteCallback, note);
            }
        });

        CountDownTimer countDownTimer = new CountDownTimer(GAME_LENGTH, CLOCK_UPDATE_RATE) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                clockCallback.onClockTick(new ClockViewModel(secondsLeft + "s"));
            }

            @Override
            public void onFinish() {
                GameOverViewModel viewModel = new GameOverViewModel("GAME OVER");
                gameCompleteCallback.onGameComplete(viewModel);
            }
        };
        countDownTimer.start();
        sequence = songSequenceFactory.maryHadALittleLamb();
        callback.onGameStarted(createRoundEndViewModel(sequence)); // TODO wrong view model
    }

    private double frequencyFor(Note note) {
        return 440 * Math.pow(2, (note.midi() - 69) * 1f / 12);
    }

    @Override
    public void startup() {
        piano.open();
    }

    private void playGameRound(
        RoundCallback roundCallback,
        SongCompleteCallback songCompleteCallback,
        Note note
    ) {
        roundCallback.onRoundEnd(createRoundEndViewModel(sequence));
        int currentPosition = sequence.position();
        Note expectedNote = sequence.get(currentPosition);
        if (currentPosition == sequence.length() - 1 && note.equals(expectedNote)) {
            songCompleteCallback.onSongComplete();
            return;
        }

        if (note.equals(expectedNote)) {
            score += 7;

            this.sequence = new Sequence.Builder(sequence).withLatestError(null).atPosition(currentPosition + 1).build();
            roundCallback.onRoundSuccess(createSuccessViewModel(sequence));
        } else {
            score -= 3;

            Sequence updatedSequence = new Sequence.Builder(sequence).withLatestError(note).build();
            roundCallback.onRoundError(createErrorViewModel(updatedSequence));
        }
    }

    private RoundEndViewModel createRoundEndViewModel(Sequence sequence) {
        String successMessage = getSuccessMessage(sequence);
        String errorMessage = getErrorMessage(sequence);

        String currentNoteFormatted = currentNote(sequence);
        String nextNoteFormatted = nextNote(sequence);

        return new RoundEndViewModel(
            sequence,
            currentNoteFormatted,
            nextNoteFormatted,
            successMessage
        );
    }

    private RoundSuccessViewModel createSuccessViewModel(Sequence sequence) {
        return new RoundSuccessViewModel(score);
    }

    private String currentNote(Sequence sequence) {
        Note note = sequence.get(sequence.position());
        return pitchNotationFormatter.format(note);
    }

    private String nextNote(Sequence sequence) {
        if (sequence.position() + 1 < sequence.length()) {
            String nextNote = pitchNotationFormatter.format(sequence.get(sequence.position() + 1));
            return String.format(Locale.US, NEXT_NOTE_HINT_FORMAT, nextNote);
        } else {
            return "";
        }
    }

    private RoundErrorViewModel createErrorViewModel(Sequence sequence) {
        String errorMessage = getErrorMessage(sequence);

        Note errorNote = sequence.latestError();
        boolean isSharpError = pitchNotationFormatter.format(errorNote).endsWith(SHARP_SYMBOL);

        return new RoundErrorViewModel(
            sequence,
            errorMessage,
            isSharpError,
            score
        );
    }

    private String getSuccessMessage(Sequence sequence) {
        if (sequence.position() > 0) {
            return String.format(Locale.US, "Woo! Keep going! (%d/%d)", sequence.position() + 1, sequence.length());
        } else {
            return "";
        }
    }

    private String getErrorMessage(Sequence sequence) {
        if (sequence.hasError()) {
            String nextNoteAsText = pitchNotationFormatter.format(sequence.get(sequence.position()));
            String latestErrorAsText = pitchNotationFormatter.format(sequence.latestError());
            return String.format(Locale.US, "Ruhroh! The correct note is %s but you played %s.", nextNoteAsText, latestErrorAsText);
        } else {
            return "";
        }
    }

    @Override
    public void shutdown() {
        piano.close();
    }

}
