package com.novoda.pianohero;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GameModelTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

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
        gameModel = new GameModel(mockSongFactory, new SimplePitchNotationFormatter(), null);
    }

    @Test
    public void whenStarted_thenStartsGame() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);

        gameModel.startGame(startCallback, roundCallback, completionCallback);

        verify(startCallback).onGameStarted(Matchers.<RoundEndViewModel>any());
    }

    @Test
    public void whenNotesPlayedCorrectly_thenIncrementsSequencePosition() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);
        gameModel.startGame(startCallback, roundCallback, completionCallback);

        gameModel.playGameRound(roundCallback, completionCallback, Note.C4);

        ArgumentCaptor<RoundEndViewModel> roundViewModelCaptor = ArgumentCaptor.forClass(RoundEndViewModel.class);
        verify(roundCallback).onRoundUpdate(roundViewModelCaptor.capture());
        assertThat(roundViewModelCaptor.getValue().getSequence().position()).isEqualTo(1);
    }

    @Test
    public void whenNotesPlayedIncorrectly_thenDoesNotIncrementSequencePosition() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);
        gameModel.startGame(startCallback, roundCallback, completionCallback);

        gameModel.playGameRound(roundCallback, completionCallback, Note.C4);
        gameModel.playGameRound(roundCallback, completionCallback, Note.E4);

        ArgumentCaptor<RoundEndViewModel> roundViewModelCaptor = ArgumentCaptor.forClass(RoundEndViewModel.class);
        verify(roundCallback, times(2)).onRoundUpdate(roundViewModelCaptor.capture());
        assertThat(roundViewModelCaptor.getValue().getSequence().position()).isEqualTo(1);
    }

    @Test
    public void whenNotesPlayedIncorrectly_thenUpdatesLatestError() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);
        gameModel.startGame(startCallback, roundCallback, completionCallback);

        gameModel.playGameRound(roundCallback, completionCallback, Note.D4);
        gameModel.playGameRound(roundCallback, completionCallback, Note.E4);

        ArgumentCaptor<RoundEndViewModel> roundViewModelCaptor = ArgumentCaptor.forClass(RoundEndViewModel.class);
        verify(roundCallback, times(2)).onRoundUpdate(roundViewModelCaptor.capture());
        assertThat(roundViewModelCaptor.getValue().getSequence().latestError()).isEqualTo(Note.E4);
    }

    @Test
    public void whenNotesPlayedCorrectly_thenClearsLatestError() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);
        gameModel.startGame(startCallback, roundCallback, completionCallback);

        gameModel.playGameRound(roundCallback, completionCallback, Note.D4);
        gameModel.playGameRound(roundCallback, completionCallback, Note.C4);

        ArgumentCaptor<RoundEndViewModel> roundViewModelCaptor = ArgumentCaptor.forClass(RoundEndViewModel.class);
        verify(roundCallback, times(2)).onRoundUpdate(roundViewModelCaptor.capture());
        assertThat(roundViewModelCaptor.getValue().getSequence().latestError()).isNull();
    }

    @Test
    public void whenFinalNotesPlayedCorrectly_thenTriggersSequenceComplete() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);
        gameModel.startGame(startCallback, roundCallback, completionCallback);

        gameModel.playGameRound(roundCallback, completionCallback, Note.C4);
        gameModel.playGameRound(roundCallback, completionCallback, Note.D4);
        gameModel.playGameRound(roundCallback, completionCallback, Note.E4);

        verify(completionCallback).onGameComplete();
    }

    @Test
    public void whenFinalNotesPlayedIncorrectly_thenDoesNotTriggerSequenceComplete() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        when(mockSongFactory.maryHadALittleLamb()).thenReturn(sequence);
        gameModel.startGame(startCallback, roundCallback, completionCallback);

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
