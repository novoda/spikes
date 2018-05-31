package com.novoda.dungeoncrawler;

class StartClock {

    private long millis = 0;

    public void start() {
        if (millis == 0) {
            millis = System.currentTimeMillis();
        }
    }

    /**
     * https://www.arduino.cc/reference/en/language/functions/time/millis/
     *
     * @return Number of milliseconds since the program started (unsigned long)
     */
    long millis() {
        if (millis == 0) {
            throw new IllegalStateException("Did you not call start? millis() is 0.");
        }
        return System.currentTimeMillis() - millis;
    }

}
