package com.novoda.pianohero;

import java.util.Locale;

public class GameModel implements GameMvp.Model {

    private final SongSequenceFactory songSequenceFactory;
    private final SimplePitchNotationFormatter pitchNotationFormatter;
    private final MidiKeyboardDriver midiKeyboardDriver;

    private Sequence sequence;

    GameModel(
            SongSequenceFactory songSequenceFactory,
            SimplePitchNotationFormatter pitchNotationFormatter,
            MidiKeyboardDriver midiKeyboardDriver) {
        this.songSequenceFactory = songSequenceFactory;
        this.pitchNotationFormatter = pitchNotationFormatter;
        this.midiKeyboardDriver = midiKeyboardDriver;
    }

    @Override
    public void startGame(StartCallback callback, final RoundCallback roundCallback, final CompletionCallback completionCallback) {
        midiKeyboardDriver.attachListener(new NoteListener() {
            @Override
            public void onPlay(Note note) {
                playGameRound(roundCallback, completionCallback, note);
            }
        });

        sequence = songSequenceFactory.maryHadALittleLamb();
        callback.onGameStarted(createViewModel(sequence));
    }

    @Override
    public void startup() {
        midiKeyboardDriver.open();
    }

    private RoundViewModel createViewModel(Sequence sequence) {
        String statusMessage = getStatusMessage(sequence);
        return new RoundViewModel(sequence, statusMessage);
    }

    private String getStatusMessage(Sequence sequence) {
        if (sequence.latestError() == null) {
            if (sequence.position() > 0) {
                return String.format(Locale.US, "Woo! Keep going! (%d/%d)", sequence.position() + 1, sequence.length());
            } else {
                return "";
            }
        } else {
            String nextNoteAsText = pitchNotationFormatter.format(sequence.get(sequence.position()));
            String latestErrorAsText = pitchNotationFormatter.format(sequence.latestError());
            return String.format(Locale.US, "Ruhroh! The correct note is %s but you played %s.", nextNoteAsText, latestErrorAsText);
        }
    }

    @Override
    public void playGameRound(
            RoundCallback roundCallback,
            CompletionCallback completionCallback,
            Note note
    ) {
        int currentPosition = sequence.position();
        Note expectedNote = sequence.get(currentPosition);
        if (currentPosition == sequence.length() - 1 && note.equals(expectedNote)) {
            completionCallback.onGameComplete();
            return;
        }

        if (note.equals(expectedNote)) {
            this.sequence = new Sequence.Builder(sequence).withLatestError(null).atPosition(currentPosition + 1).build();
            roundCallback.onRoundUpdate(createViewModel(sequence));
        } else {
            Sequence updatedSequence = new Sequence.Builder(sequence).withLatestError(note).build();
            roundCallback.onRoundUpdate(createViewModel(updatedSequence));
        }
    }

    @Override
    public void shutdown() {
        midiKeyboardDriver.close();
    }

}
