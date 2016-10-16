package com.novoda.wallpaper.droidcon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TimeOfDayTest {

    @Test
    public void sixIsDawn() {
        TimeOfDay time = TimeOfDay.forHourOfDay(6);
        assertThat(time).isEqualTo(TimeOfDay.DAWN);
    }

    @Test
    public void tweleveIsDay() {
        TimeOfDay time = TimeOfDay.forHourOfDay(12);
        assertThat(time).isEqualTo(TimeOfDay.DAY);
    }

    @Test
    public void sixteenIsDusk() {
        TimeOfDay time = TimeOfDay.forHourOfDay(16);
        assertThat(time).isEqualTo(TimeOfDay.DUSK);
    }

    @Test
    public void twentyTwoIsNight() {
        TimeOfDay time = TimeOfDay.forHourOfDay(22);
        assertThat(time).isEqualTo(TimeOfDay.NIGHT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeHourThrowsException() {
        TimeOfDay.forHourOfDay(-490);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooBigHourThrowsException() {
        TimeOfDay.forHourOfDay(490);
    }

}
