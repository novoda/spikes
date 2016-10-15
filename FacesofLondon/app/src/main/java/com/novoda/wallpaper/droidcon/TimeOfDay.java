package com.novoda.wallpaper.droidcon;

enum TimeOfDay {
    DAWN(4),
    DAY(10),
    DUSK(16),
    NIGHT(22);

    private final int hourOfDay;

    TimeOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    int hourOfDay() {
        return hourOfDay;
    }
}
