package com.novoda.wallpaper.droidcon;

class NextTimeOfDayCalculator implements Calculator {

    private int index = 0;

    @Override
    public TimeOfDay currentTimeOfDay() {
        TimeOfDay[] values = TimeOfDay.values();
        index = (index + 1) % values.length;
        return values[index];
    }
}
