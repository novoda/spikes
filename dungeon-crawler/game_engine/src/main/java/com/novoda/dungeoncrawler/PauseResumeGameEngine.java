package com.novoda.dungeoncrawler;

import com.yheriatovych.reductor.Store;

class PauseResumeGameEngine implements GameEngine {

    private static final int MIN_REDRAW_INTERVAL = 30;    // Min redraw interval (ms) 33 = 30fps / 16 = 63fps

    private final Store<Redux.GameState> store;
    private final JoystickActuator inputActuator;
    private final StartClock clock;

    PauseResumeGameEngine(Store<Redux.GameState> store, JoystickActuator inputActuator, StartClock clock) {
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
        for (Particle particle : gameState.particlePool) {
            if (particle.isAlive()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void loadLevel() {
        store.dispatch(Redux.GameActions.nextLevel(clock.millis()));
    }

    @Override
    public void loop() {
        Redux.GameState state = store.getState();
        long frameTime = clock.millis();
        if (frameTime - state.frameTime >= MIN_REDRAW_INTERVAL) {
            JoystickActuator.JoyState joyState = inputActuator.getInput();
            int joyTilt = joyState.tilt;
            int joyWobble = joyState.wobble;
            store.dispatch(Redux.GameActions.nextFrame(frameTime, joyTilt, joyWobble));
        }
    }

    public void pause() {
        store.dispatch(Redux.GameActions.pauseGame(clock.millis()));
    }

    public void resume() {
        store.dispatch(Redux.GameActions.resumeGame(clock.millis()));
    }
}
