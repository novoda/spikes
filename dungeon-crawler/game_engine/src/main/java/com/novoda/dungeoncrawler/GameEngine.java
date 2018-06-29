package com.novoda.dungeoncrawler;

public interface GameEngine {

    static double map(int valueCoord1,
                      int startCoord1, int endCoord1,
                      int startCoord2, int endCoord2) {

        double epsilon = 1e-12;
        if (Math.abs(endCoord1 - startCoord1) < epsilon) {
            throw new ArithmeticException("/ 0");
        }

        double offset = startCoord2;
        float ratio = (float) (endCoord2 - startCoord2) / (endCoord1 - startCoord1);
        return ratio * (valueCoord1 - startCoord1) + offset;
    }

    void loadLevel();

    void loop();
}
