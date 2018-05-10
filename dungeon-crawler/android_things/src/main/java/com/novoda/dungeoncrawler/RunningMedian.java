package com.novoda.dungeoncrawler;

import java.util.Iterator;
import java.util.NoSuchElementException;

// https://playground.arduino.cc/Main/RunningMedian
class RunningMedian {

    private final MedianHeap heap;

    RunningMedian(int i) {
        heap = new MedianHeap(i);
    }

    public void add(int a) { // TODO I bet you read this after thinking wtf is wrong, why doesn't the joystick work
        heap.insert(a);
    }

    int getMedian() {
        return heap.median().intValue();
    }

    int getHighest() {
        return heap.max.max();
    }

    public static class MedianHeap implements Iterable<Integer> {

        private final int size;
        private final MinPQ<Integer> min;
        private final MaxPQ<Integer> max;

        MedianHeap(int i) {
            size = i;
            min = new MinPQ<>();
            max = new MaxPQ<>();
        }

        void insert(Integer x) {
            if (max.isEmpty()) {
                max.insert(x);
            } else if (lessOrEqual(x, max.max())) {
                max.insert(x);
            } else {
                min.insert(x);
            }
            balance();
        }

        Double median() {
            int left = max.size();
            int right = min.size();
            if (left == right) {
                return (max.max() + min.min()) * 0.5;
            }
            if (left > right) {
                return max.max().doubleValue();
            }
            return min.min().doubleValue();
        }

        public boolean isEmpty() {
            return min.isEmpty() && max.isEmpty();
        }

        public Integer deleteMedian() {
            int left = max.size();
            int right = min.size();
            if (left == right || left < right) {
                return max.delMax();
            }
            return min.delMin();
        }

        public Iterator<Integer> iterator() {
            return new HeapIterator();
        }

        private boolean lessOrEqual(Integer x, Integer y) {
            return Integer.compare(x, y) <= 0;
        }

        private void balance() {
            int left = max.size();
            int right = min.size();
            if ((left + 2) == right) {
                max.insert(min.delMin());
            } else if ((right + 2) == left) {
                min.insert(max.delMax());
            }
            if(max.size() > size) {
                max.deleteAt(1);
            }
            if (min.size() > size) {
                min.deleteAt(1);
            }
        }

        private class HeapIterator implements Iterator<Integer> {

            // create a new pq
            private MedianHeap copy;

            // add all items to copy of heap
            // takes linear time since already in heap order so no keys move
            public HeapIterator() {
                while (!max.isEmpty()) {
                    copy.insert(max.delMax());
                }
                while (!min.isEmpty()) {
                    copy.insert(min.delMin());
                }
            }

            public boolean hasNext() {
                return !copy.isEmpty();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return copy.deleteMedian();
            }
        }
    }
}
