package com.novoda.tpbot.controller;

import com.novoda.notils.logger.simple.Log;

interface OnDirectionPressedListener {
    OnDirectionPressedListener NO_OP = new OnDirectionPressedListener() {
        @Override
        public void onDirectionPressed(Direction direction) {
            Log.w("onDirectionPressed() but no listener was set");
        }

        @Override
        public void onDirectionReleased(Direction direction) {
            Log.w("onDirectionReleased() but no listener was set");
        }

        @Override
        public void onLazersPressed() {
            Log.w("onLazersPressed() but no listener was set");
        }
    };

    void onDirectionPressed(Direction direction);

    void onDirectionReleased(Direction direction);

    void onLazersPressed();
}
