package com.novoda.pianohero;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GameModel implements GameMvp.Model {

    private final SongSequenceFactory songSequenceFactory;
    private final SimplePitchNotationFormatter pitchNotationFormatter;

    private Sequence sequence;

    GameModel(
        SongSequenceFactory songSequenceFactory,
        SimplePitchNotationFormatter pitchNotationFormatter) {
        this.songSequenceFactory = songSequenceFactory;
        this.pitchNotationFormatter = pitchNotationFormatter;
    }

    @Override
    public void startGame(StartCallback callback) {
        sequence = songSequenceFactory.maryHadALittleLamb();

        checkSequenceIsSimpleElseThrow(sequence);

        callback.onGameStarted(createViewModel(sequence));
    }

    private void checkSequenceIsSimpleElseThrow(Sequence sequence) {
        for (int i = 0; i < sequence.length(); i++) {
            Notes notes = sequence.get(i);
            if (notes.count() > 1) {
                throw new IllegalArgumentException("Sequence contains chords, that's not simple enough");
            }
            for (Note note : notes.notes()) {
                if (pitchNotationFormatter.format(note).endsWith("#")) {
                    throw new IllegalArgumentException("Sequence contains sharps, that's not simple enough");
                }
            }
        }
    }

    @NonNull
    private RoundViewModel createViewModel(Sequence updatedSequence) {
        int nextNotesPosition = updatedSequence.position();
        List<String> notations = getNotations(nextNotesPosition);
        String statusMessage = getStatusMessage(updatedSequence);
        return new RoundViewModel(notations, statusMessage);
    }

    private List<String> getNotations(int position) {
        List<String> notations = new ArrayList<>();
        for (Note note : sequence.get(position)) {
            String simpleNotation = pitchNotationFormatter.format(note);
            notations.add(simpleNotation);
        }
        return notations;
    }

    private String getStatusMessage(Sequence sequence) {
        if (sequence.latestError().count() == 0) {
            if (sequence.position() > 0) {
                return String.format(Locale.US, "Woo! Keep going! (%d/%d)", sequence.position() + 1, sequence.length());
            } else {
                return "";
            }
        } else {
            return "Ruhroh, try again!";
        }
    }

    @Override
    public void playGameRound(
        RoundCallback roundCallback,
        CompletionCallback completionCallback,
        Note... notesCollection) {
        Notes notes = new Notes(notesCollection);
        playGameRound(roundCallback, completionCallback, notes);
    }

    private void playGameRound(RoundCallback roundCallback, CompletionCallback completionCallback, Notes notes) {
        int currentPosition = sequence.position();
        Notes expectedNotes = sequence.get(currentPosition);
        if (currentPosition == sequence.length() - 1 && notes.equals(expectedNotes)) {
            completionCallback.onGameComplete();
            return;
        }

        if (notes.equals(expectedNotes)) {
            this.sequence = new Sequence.Builder(sequence).atPosition(currentPosition + 1).build();
            roundCallback.onRoundUpdate(createViewModel(sequence));
        } else {
            Sequence updatedSequence = new Sequence.Builder(sequence).withLatestError(notes).build();
            roundCallback.onRoundUpdate(createViewModel(updatedSequence));
        }
    }

}
