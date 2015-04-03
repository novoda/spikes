package com.novoda.landingstrip;

class MutableCoordinates implements Coordinates {

    private float start;
    private float end;

    void setStart(float start) {
        this.start = start;
    }

    void setEnd(float end) {
        this.end = end;
    }

    @Override
    public float getStart() {
        return start;
    }

    @Override
    public float getEnd() {
        return end;
    }
}
