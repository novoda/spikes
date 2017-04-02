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

public class BrainTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    SequenceDisplayer sequenceDisplayer;

    @Mock
    Brain.Callback callback;

    Brain brain;

    @Before
    public void setUp() {
        brain = new Brain(sequenceDisplayer);
    }

    @Test
    public void whenStarted_thenDisplaysUnmodifiedSequence() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);

        brain.start(sequence);

        verify(sequenceDisplayer).display(sequence);
    }

    @Test
    public void whenNotesPlayedCorrectly_thenIncrementsSequencePosition() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        brain.start(sequence);

        brain.onNotesPlayed(Note.C4);

        ArgumentCaptor<Sequence> sequenceCaptor = ArgumentCaptor.forClass(Sequence.class);
        verify(sequenceDisplayer, times(2)).display(sequenceCaptor.capture());
        assertThat(sequenceCaptor.getValue().position()).isEqualTo(1);
    }

    @Test
    public void whenNotesPlayedIncorrectly_thenDoesNotIncrementSequencePosition() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        brain.start(sequence);

        brain.onNotesPlayed(Note.D4);

        ArgumentCaptor<Sequence> sequenceCaptor = ArgumentCaptor.forClass(Sequence.class);
        verify(sequenceDisplayer, times(2)).display(sequenceCaptor.capture());
        assertThat(sequenceCaptor.getValue().position()).isEqualTo(0);
    }

    @Test
    public void whenNotesPlayedIncorrectly_thenUpdatesLatestError() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        brain.start(sequence);

        brain.onNotesPlayed(Note.D4);
        brain.onNotesPlayed(Note.E4);

        ArgumentCaptor<Sequence> sequenceCaptor = ArgumentCaptor.forClass(Sequence.class);
        verify(sequenceDisplayer, times(3)).display(sequenceCaptor.capture());
        assertThat(sequenceCaptor.getValue().latestError()).isEqualTo(new Notes(Note.E4));
    }

    @Test
    public void whenNotesPlayedCorrectly_thenClearsLatestError() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        brain.start(sequence);

        brain.onNotesPlayed(Note.D4);
        brain.onNotesPlayed(Note.C4);

        ArgumentCaptor<Sequence> sequenceCaptor = ArgumentCaptor.forClass(Sequence.class);
        verify(sequenceDisplayer, times(3)).display(sequenceCaptor.capture());
        assertThat(sequenceCaptor.getValue().latestError()).isEqualTo(Notes.EMPTY);
    }

    @Test
    public void whenFinalNotesPlayedCorrectly_thenTriggersSequenceComplete() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        brain.attach(callback);
        brain.start(sequence);

        brain.onNotesPlayed(Note.C4);
        brain.onNotesPlayed(Note.D4);
        brain.onNotesPlayed(Note.E4);

        verify(callback).onSequenceComplete();
    }

    @Test
    public void whenFinalNotesPlayedIncorrectly_thenDoesNotTriggerSequenceComplete() {
        Sequence sequence = make(Note.C4, Note.D4, Note.E4);
        brain.attach(callback);
        brain.start(sequence);

        brain.onNotesPlayed(Note.C4);
        brain.onNotesPlayed(Note.D4);
        brain.onNotesPlayed(Note.F4);

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