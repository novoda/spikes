package com.novoda.dungeoncrawler;

import com.yheriatovych.reductor.Store;

class LoopObserver {

    LoopObserver(Store<Redux.GameState> store,
                 JoystickActuator joystick,
                 AttackMonitor attackMonitor,
                 MovementMonitor movementMonitor,
                 DeathMonitor deathMonitor,
                 KillMonitor killMonitor) {
        store.subscribe(gameState -> {
            if (gameState.stage == Stage.PLAY) {
                if (gameState.attacking) {
                    attackMonitor.onAttack();
                } else {
                    movementMonitor.onMove(joystick.getInput().tilt);
                }
            } else if (gameState.stage == Stage.DEAD) {
                deathMonitor.onDeath();
            }
        });
    }

    interface AttackMonitor {
        void onAttack();
    }

    interface KillMonitor {
        void onKill();
    }

    interface MovementMonitor {
        void onMove(int velocity);
    }

    interface DeathMonitor {
        void onDeath();
    }
}
