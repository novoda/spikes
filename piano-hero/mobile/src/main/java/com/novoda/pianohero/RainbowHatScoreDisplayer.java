package com.novoda.pianohero;

import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;

import java.io.IOException;

class RainbowHatScoreDisplayer implements ScoreDisplayer, AndroidThing {

    private AlphanumericDisplay display;

    @Override
    public void open() {
        try {
            display = new AlphanumericDisplay("I2C1");
            display.setEnabled(true);
            display.clear();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot open rainbow hat display");
        }
    }

    @Override
    public void close() {
        try {
            display.setEnabled(false);
            display.close();
        } catch (IOException e) {
            throw new IllegalStateException("couldn't close display");
        }
    }

    @Override
    public void display(CharSequence score) {
        try {
            display.display(score.toString());
        } catch (IOException e) {
            throw new IllegalStateException("couldn't display score: " + score);
        }
    }

    @Override
    public void hide() {
        try {
            display.clear();
        } catch (IOException e) {
            throw new IllegalStateException("couldn't clear score");
        }
    }
}
