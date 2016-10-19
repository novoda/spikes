package com.novoda.wallpaper.droidcon;

import java.util.Calendar;

class TimeOfDayCalculator {

    TimeOfDay currentTimeOfDay() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        return TimeOfDay.forHourOfDay(hourOfDay);
    }
}
