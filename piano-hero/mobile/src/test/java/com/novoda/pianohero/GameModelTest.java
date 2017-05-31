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
import static org.mockito.Mockito.when;

public class GameModelTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    // TODO: these tests are assuming this callback is used but it isn't anymore
    // rather than delete, would rather update the viewmodel to use the sequence and then use
    // that callback in the verifies - the logic should be the same
    @Mock
    OnSequenceUpdatedCallback onSequenceUpdatedCallback;

    @Mock
    GameMvp.Model.StartCallback startCallback;

    @Mock
    GameMvp.Model.RoundCallback roundCallback;

    @Mock
    GameMvp.Model.CompletionCallback completionCallback;

    private GameModel gameModel;

    @Mock
    private SongSequenceFactory mockSongFactory;

    @Before
    public void setUp() {
        gameModel = new GameModel(mockSongFactory, new SimplePitchNotationFormatter());
    }

    @Test
    public void whenStarted_thenDisplaysUnmodifiedSequence() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);

        gameModel.startGame(startCallback);

        verify(onSequenceUpdatedCallback).onNext(sequence);
    }

    @Test
    public void whenNotesPlayedCorrectly_thenIncrementsSequencePosition() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);

        gameModel.startGame(startCallback);

        gameModel.playGameRound(roundCallback, completionCallback, Note.C4);

        ArgumentCaptor<Sequence> sequenceCaptor = ArgumentCaptor.forClass(Sequence.class);
        verify(onSequenceUpdatedCallback, times(2)).onNext(sequenceCaptor.capture());
        assertThat(sequenceCaptor.getValue().position()).isEqualTo(1);
    }

    @Test
    public void whenNotesPlayedIncorrectly_thenDoesNotIncrementSequencePosition() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);
        gameModel.startGame(startCallback);

        gameModel.playGameRound(roundCallback, completionCallback, Note.C4);
        gameModel.playGameRound(roundCallback, completionCallback, Note.E4);

        ArgumentCaptor<Sequence> sequenceCaptor = ArgumentCaptor.forClass(Sequence.class);
        verify(onSequenceUpdatedCallback, times(3)).onNext(sequenceCaptor.capture());
        assertThat(sequenceCaptor.getValue().position()).isEqualTo(1);
    }

    @Test
    public void whenNotesPlayedIncorrectly_thenUpdatesLatestError() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);
        gameModel.startGame(startCallback);

        gameModel.playGameRound(roundCallback, completionCallback, Note.D4);
        gameModel.playGameRound(roundCallback, completionCallback, Note.E4);

        ArgumentCaptor<Sequence> sequenceCaptor = ArgumentCaptor.forClass(Sequence.class);
        verify(onSequenceUpdatedCallback, times(3)).onNext(sequenceCaptor.capture());
        assertThat(sequenceCaptor.getValue().latestError()).isEqualTo(Note.E4);
    }

    @Test
    public void whenNotesPlayedCorrectly_thenClearsLatestError() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);
        gameModel.startGame(startCallback);

        gameModel.playGameRound(roundCallback, completionCallback, Note.D4);
        gameModel.playGameRound(roundCallback, completionCallback, Note.C4);

        ArgumentCaptor<Sequence> sequenceCaptor = ArgumentCaptor.forClass(Sequence.class);
        verify(onSequenceUpdatedCallback, times(3)).onNext(sequenceCaptor.capture());
        assertThat(sequenceCaptor.getValue().latestError()).isNull();
    }

    @Test
    public void whenFinalNotesPlayedCorrectly_thenTriggersSequenceComplete() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);
        gameModel.startGame(startCallback);

        gameModel.playGameRound(roundCallback, completionCallback, Note.C4);
        gameModel.playGameRound(roundCallback, completionCallback, Note.D4);
        gameModel.playGameRound(roundCallback, completionCallback, Note.E4);

        verify(completionCallback).onGameComplete();
    }

    @Test
    public void whenFinalNotesPlayedIncorrectly_thenDoesNotTriggerSequenceComplete() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);
        gameModel.startGame(startCallback);

        gameModel.playGameRound(roundCallback, completionCallback, Note.C4);
        gameModel.playGameRound(roundCallback, completionCallback, Note.D4);
        gameModel.playGameRound(roundCallback, completionCallback, Note.F4);

        verify(completionCallback, never()).onGameComplete();
    }

    private Sequence make(Note... notes) {
        Sequence.Builder builder = new Sequence.Builder();
        for (Note note : notes) {
            builder.add(note);
        }
        return builder.build();
    }
}
