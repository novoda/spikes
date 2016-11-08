package com.novoda.tpbot.controller;

import com.novoda.notils.logger.simple.Log;

public interface OnDirectionPressedListener {
    OnDirectionPressedListener NO_OP = new OnDirectionPressedListener() {
        @Override
        public void onDirectionPressed(BotDirection direction) {
            Log.w("onDirectionPressed() but no listener was set");
        }

        @Override
        public void onDirectionReleased(BotDirection direction) {
            Log.w("onDirectionReleased() but no listener was set");
        }
    };

    void onDirectionPressed(BotDirection direction);

    void onDirectionReleased(BotDirection direction);
}
