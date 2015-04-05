package com.novoda.landingstrip;

class State {

    private float offset;
    private int position;

    void updateOffset(float offset) {
        this.offset = offset;
    }

    void updatePosition(int position) {
        this.position = position;
    }

    float getOffset() {
        return offset;
    }

    int getPosition() {
        return position;
    }
}
