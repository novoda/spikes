package com.novoda.dungeoncrawler;

import com.yheriatovych.reductor.Store;

class GameEngine {

    private static final int MIN_REDRAW_INTERVAL = 33;    // Min redraw interval (ms) 33 = 30fps / 16 = 63fps

    private final Store<Redux.GameState> store;
    private final JoystickActuator inputActuator;
    private final StartClock clock;

    GameEngine(Store<Redux.GameState> store, JoystickActuator inputActuator, StartClock clock) {
        this.store = store;
        this.inputActuator = inputActuator;
        this.clock = clock;
        store.subscribe(gameState -> {
            long frameTime = clock.millis();
            if (gameState.stage == Stage.LEVEL_COMPLETE) {
                if (frameTime > gameState.stageStartTime + 1200) {
                    store.dispatch(Redux.GameActions.nextLevel(clock.millis()));
                }
            } else if (gameState.stage == Stage.GAME_COMPLETE) {
                if (frameTime > gameState.stageStartTime + 5500) {
                    store.dispatch(Redux.GameActions.nextLevel(clock.millis()));
                }
            } else if (gameState.stage == Stage.DEAD) {
                if (areAllParticlesDeactive(gameState)) {
                    store.dispatch(Redux.GameActions.restartLevel(clock.millis()));
                }
            }
        });
    }

    private static boolean areAllParticlesDeactive(Redux.GameState gameState) {
        for (Particle particle : gameState.PARTICLE_POOL) {
            if (particle.isAlive()) {
                return false;
            }
        }
        return true;
    }

    void loadLevel() {
        store.dispatch(Redux.GameActions.nextLevel(clock.millis()));
    }

    void loop() {
        Redux.GameState state = store.getState();
        long frameTime = clock.millis();
        if (frameTime - state.frameTime >= MIN_REDRAW_INTERVAL) {
            JoystickActuator.JoyState joyState = inputActuator.getInput();
            int joyTilt = joyState.tilt;
            int joyWobble = joyState.wobble;
            store.dispatch(Redux.GameActions.nextFrame(frameTime, joyTilt, joyWobble));
        }
    }

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
}
