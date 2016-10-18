package com.novoda.wallpaper.droidcon;

class TimeOfDayCalculator {

    private static int flag = 0;

    TimeOfDay currentTimeOfDay() {
        TimeOfDay[] values = TimeOfDay.values();
        flag++;
        if (flag >= values.length) {
            flag = 0;
        }
        return values[flag];
//        Calendar calendar = Calendar.getInstance();
//        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
//        return TimeOfDay.forHourOfDay(hourOfDay);
    }
}
