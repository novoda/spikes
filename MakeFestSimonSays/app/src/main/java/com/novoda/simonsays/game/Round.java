package com.novoda.simonsays.game;

import android.view.View;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Round {

    private final int level;
    private final ViewSequence viewSequence;
    private final KeySequence keySequence;

    public Round(int level, ViewSequence viewSequence, KeySequence keySequence) {
        this.level = level;
        this.viewSequence = viewSequence;
        this.keySequence = keySequence;
    }

    public ViewSequence getViewSequence() {
        return viewSequence;
    }

    public KeySequence getKeySequence() {
        return keySequence;
    }

    public int getScore() {
        return level - 1;
    }

    public int getNextLevel() {
        return level + 1;
    }

    public long getSpeed() {
        return TimeUnit.SECONDS.toMillis(2);
    }

    public static class ViewSequence implements Iterable<View> {

        private final List<View> views;

        private int count;

        public ViewSequence(List<View> views) {
            this.views = views;
        }

        public View next() {
            return views.get(count++);
        }

        public boolean isComplete() {
            return count == views.size();
        }

        @Override
        public Iterator<View> iterator() {
            return views.iterator();
        }
    }

    public static class KeySequence implements Iterable<Integer> {

        private final List<Integer> keys;

        public KeySequence(List<Integer> keys) {
            this.keys = keys;
        }

        public int size() {
            return keys.size();
        }

        public Integer get(int position) {
            return keys.get(position);
        }

        @Override
        public Iterator<Integer> iterator() {
            return keys.iterator();
        }
    }
}
