package com.novoda.dungeoncrawler;

class RunningMedian {

    private boolean _sorted;
    private int _size;
    private int _cnt;
    private int _idx;

    private final float[] _ar;
    private final int[] _p;

    RunningMedian(int size) {
        _size = size;
        _p = new int[size];
        _ar = new float[size];
    }

    // adds a new value to the data-set
// or overwrites the oldest if full.
    void add(float value) {
        _ar[_idx++] = value;
        if (_idx >= _size) {
            _idx = 0; // wrap around
        }
        if (_cnt < _size) {
            _cnt++;
        }
        _sorted = false;
    }

    int getMedian() {
        if (_cnt == 0) {
            return 0;
        }

        if (!_sorted) {
            sort();
        }

        if ((_cnt & 0x01) != 0) {
            return (int) _ar[_p[_cnt / 2]];
        } else {
            return (int) ((_ar[_p[_cnt / 2]] + _ar[_p[_cnt / 2 - 1]]) / 2);
        }
    }

    private float getSortedElement(int n) {
        if ((_cnt == 0) || (n >= _cnt)) {
            return 0;
        }

        if (!_sorted) {
            sort();
        }
        return _ar[_p[n]];
    }

    private void sort() {
        // bubble sort with flag
        for (int i = 0; i < _cnt - 1; i++) {
            boolean flag = true;
            for (int j = 1; j < _cnt - i; j++) {
                if (_ar[_p[j - 1]] > _ar[_p[j]]) {
                    int t = _p[j - 1];
                    _p[j - 1] = _p[j];
                    _p[j] = t;
                    flag = false;
                }
            }
            if (flag) {
                break;
            }
        }
        _sorted = true;
    }

    int getHighest() {
        return (int) getSortedElement(_cnt - 1);
    }
}
