package com.novoda.tpbot.controls;

import com.novoda.tpbot.model.Direction;

public interface ControllerListener {

    ControllerListener NO_OP = new ControllerListener() {

        @Override
        public void onDirectionPressed(Direction direction) {
            // no-op
        }

        @Override
        public void onDirectionReleased(Direction direction) {
            // no-op
        }

        @Override
        public void onLazersFired() {
            // no-op
        }

        @Override
        public void onLazersReleased() {
            // no-op
        }

    };

    void onDirectionPressed(Direction direction);

    void onDirectionReleased(Direction direction);

    void onLazersFired();

    void onLazersReleased();
}
