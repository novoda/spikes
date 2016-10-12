package com.novoda.wallpaper.droidcon;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class PartOfDayCalculatorTest {
    private PartOfDayCalculator factory = new PartOfDayCalculator();

    @Test
    public void sixIsDawn() {
        TimeOfDay time = factory.forHourOfDay(6);
        assertThat(time).isEqualTo(TimeOfDay.DAWN);
    }

    @Test
    public void tweleveIsDay() {
        TimeOfDay time = factory.forHourOfDay(12);
        assertThat(time).isEqualTo(TimeOfDay.DAY);
    }

    @Test
    public void sixteenIsDusk() {
        TimeOfDay time = factory.forHourOfDay(16);
        assertThat(time).isEqualTo(TimeOfDay.DUSK);
    }

    @Test
    public void twentyTwoIsDusk() {
        TimeOfDay time = factory.forHourOfDay(22);
        assertThat(time).isEqualTo(TimeOfDay.NIGHT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeHourThrowsException() {
        factory.forHourOfDay(-490);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooBigHourThrowsException() {
        factory.forHourOfDay(490);
    }
}
