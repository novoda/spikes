package com.novoda.dungeoncrawlercompanion

import com.novoda.dungeoncrawler.GameEngine
import com.novoda.dungeoncrawler.JoystickActuator
import com.novoda.dungeoncrawler.Redux
import com.novoda.dungeoncrawler.Stage
import com.novoda.dungeoncrawler.StartClock
import com.yheriatovych.reductor.Store

class ReplayGameEngine(private val store: Store<Redux.GameState>, private val inputActuator: JoystickActuator, private val clock: StartClock) : GameEngine {

    init {
        store.subscribe { gameState ->
            val frameTime = clock.millis()
            if (gameState.stage == Stage.LEVEL_COMPLETE) {
                if (frameTime > gameState.stageStartTime + 1200) {
                    store.dispatch(Redux.GameActions.nextLevel(clock.millis()))
                }
            } else if (gameState.stage == Stage.GAME_COMPLETE) {
                if (frameTime > gameState.stageStartTime + 5500) {
                    store.dispatch(Redux.GameActions.nextLevel(clock.millis()))
                }
            } else if (gameState.stage == Stage.DEAD) {
                if (areAllParticlesDeactive()) {
                    store.dispatch(Redux.GameActions.restartLevel(clock.millis()))
                }
            }
        }
    }

    override fun loadLevel() {
        store.dispatch(Redux.GameActions.nextLevel(clock.millis()))
    }

    override fun loop() {
        val state = store.state
        val frameTime = clock.millis()
        if (frameTime - state.frameTime >= MIN_REDRAW_INTERVAL) {
            val joyState = inputActuator.input
            val joyTilt = joyState.tilt
            val joyWobble = joyState.wobble
            store.dispatch(Redux.GameActions.nextFrame(frameTime, joyTilt.toDouble(), joyWobble.toDouble()))
        }
    }

    companion object {

        private val MIN_REDRAW_INTERVAL = 33    // Min redraw interval (ms) 33 = 30fps / 16 = 63fps

        private fun areAllParticlesDeactive(): Boolean {
            for (particle in Redux.GameState.PARTICLE_POOL) {
                if (particle.isAlive) {
                    return false
                }
            }
            return true
        }

    }
}
