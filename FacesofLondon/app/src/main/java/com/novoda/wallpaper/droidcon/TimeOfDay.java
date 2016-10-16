package com.novoda.wallpaper.droidcon;

import java.util.Locale;

enum TimeOfDay {
    DAWN(4),
    DAY(10),
    DUSK(16),
    NIGHT(22);

    private final int hourOfDay;

    TimeOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public static TimeOfDay forHourOfDay(int hourOfDay) {
        mustBeValidHourOfDay(hourOfDay);
        if (hourOfDay >= DAWN.hourOfDay && hourOfDay < DAY.hourOfDay) {
            return DAWN;
        } else if (hourOfDay >= DAY.hourOfDay && hourOfDay < DUSK.hourOfDay) {
            return DAY;
        } else if (hourOfDay >= DUSK.hourOfDay && hourOfDay < NIGHT.hourOfDay) {
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
