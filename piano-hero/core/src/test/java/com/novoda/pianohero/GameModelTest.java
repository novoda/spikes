package com.novoda.pianohero;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GameModelTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    OnSequenceUpdatedCallback onSequenceUpdatedCallback;

    @Mock
    GameModel.Callback callback;

    GameModel gameModel;

    @Before
    public void setUp() {
        gameModel = new GameModel(new SongSequenceFactory(), onSequenceUpdatedCallback);
    }

    @Test
    public void whenStarted_thenDisplaysUnmodifiedSequence() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);

        gameModel.start(sequence);

        verify(onSequenceUpdatedCallback).onNext(sequence);
    }

    @Test
    public void whenNotesPlayedCorrectly_thenIncrementsSequencePosition() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        gameModel.start(sequence);

        gameModel.onNotesPlayed(Note.C4);

        ArgumentCaptor<Sequence> sequenceCaptor = ArgumentCaptor.forClass(Sequence.class);
        verify(onSequenceUpdatedCallback, times(2)).onNext(sequenceCaptor.capture());
        assertThat(sequenceCaptor.getValue().position()).isEqualTo(1);
    }

    @Test
    public void whenNotesPlayedIncorrectly_thenDoesNotIncrementSequencePosition() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        gameModel.start(sequence);

        gameModel.onNotesPlayed(Note.C4);
        gameModel.onNotesPlayed(Note.E4);

        ArgumentCaptor<Sequence> sequenceCaptor = ArgumentCaptor.forClass(Sequence.class);
        verify(onSequenceUpdatedCallback, times(3)).onNext(sequenceCaptor.capture());
        assertThat(sequenceCaptor.getValue().position()).isEqualTo(1);
    }

    @Test
    public void whenNotesPlayedIncorrectly_thenUpdatesLatestError() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        gameModel.start(sequence);

        gameModel.onNotesPlayed(Note.D4);
        gameModel.onNotesPlayed(Note.E4);

        ArgumentCaptor<Sequence> sequenceCaptor = ArgumentCaptor.forClass(Sequence.class);
        verify(onSequenceUpdatedCallback, times(3)).onNext(sequenceCaptor.capture());
        assertThat(sequenceCaptor.getValue().latestError()).isEqualTo(new Notes(Note.E4));
    }

    @Test
    public void whenNotesPlayedCorrectly_thenClearsLatestError() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        gameModel.start(sequence);

        gameModel.onNotesPlayed(Note.D4);
        gameModel.onNotesPlayed(Note.C4);

        ArgumentCaptor<Sequence> sequenceCaptor = ArgumentCaptor.forClass(Sequence.class);
        verify(onSequenceUpdatedCallback, times(3)).onNext(sequenceCaptor.capture());
        assertThat(sequenceCaptor.getValue().latestError()).isEqualTo(Notes.EMPTY);
    }

    @Test
    public void whenFinalNotesPlayedCorrectly_thenTriggersSequenceComplete() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        gameModel.attach(callback);
        gameModel.start(sequence);

        gameModel.onNotesPlayed(Note.C4);
        gameModel.onNotesPlayed(Note.D4);
        gameModel.onNotesPlayed(Note.E4);

        verify(callback).onSequenceComplete();
    }

    @Test
    public void whenFinalNotesPlayedIncorrectly_thenDoesNotTriggerSequenceComplete() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        gameModel.attach(callback);
        gameModel.start(sequence);

        gameModel.onNotesPlayed(Note.C4);
        gameModel.onNotesPlayed(Note.D4);
        gameModel.onNotesPlayed(Note.F4);

        verify(callback, never()).onSequenceComplete();
    }

    private Sequence make(Note... notes) {
        Sequence.Builder builder = new Sequence.Builder();
        for (Note note : notes) {
            builder.add(note);
        }
        return builder.build();
    }
}
