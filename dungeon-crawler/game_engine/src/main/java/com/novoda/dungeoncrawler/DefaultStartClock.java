package com.novoda.dungeoncrawler;

public class DefaultStartClock implements StartClock {

    private long millis = 0;

    /**
     * https://www.arduino.cc/reference/en/language/functions/time/millis/
     *
     * @return Number of milliseconds since the first call to this method (unsigned long)
     */
    @Override
    public long millis() {
        if (millis == 0) {
            millis = System.currentTimeMillis();
        }
        return System.currentTimeMillis() - millis;
    }

}
