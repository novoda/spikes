package com.novoda.pianohero;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class SimplePitchNotationFormatterShould {

    SimplePitchNotationFormatter formatter = new SimplePitchNotationFormatter();

    @Test
    public void dropOctave() {
        assertThat(formatter.format(Note.C4)).isEqualTo("C");
        assertThat(formatter.format(Note.C5)).isEqualTo("C");
    }

    @Test
    public void retainSharp() {
        assertThat(formatter.format(Note.C4_S)).isEqualTo("C#");
    }
}
