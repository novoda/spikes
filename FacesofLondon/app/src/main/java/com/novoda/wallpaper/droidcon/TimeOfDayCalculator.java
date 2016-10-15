package com.novoda.wallpaper.droidcon;

import java.util.Calendar;
import java.util.Locale;

import static com.novoda.wallpaper.droidcon.TimeOfDay.*;

class TimeOfDayCalculator {

    TimeOfDay currentTimeOfDay() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        return forHourOfDay(hourOfDay);
    }

    TimeOfDay forHourOfDay(int hourOfDay) {
        mustBeValidHourOfDay(hourOfDay);
        if (hourOfDay >= DAWN.hourOfDay() && hourOfDay < DAY.hourOfDay()) {
            return DAWN;
        } else if (hourOfDay >= DAY.hourOfDay() && hourOfDay < DUSK.hourOfDay()) {
            return DAY;
        } else if (hourOfDay >= DUSK.hourOfDay() && hourOfDay < NIGHT.hourOfDay()) {
            return DUSK;
        } else {
            return NIGHT;
        }
    }

    private static void mustBeValidHourOfDay(int hourOfDay) {
        if (hourOfDay < 0 || hourOfDay > 24) {
            throw new IllegalArgumentException(String.format(Locale.US, "A day only has 24 hours. Got %d instead", hourOfDay));
        }
    }
}
