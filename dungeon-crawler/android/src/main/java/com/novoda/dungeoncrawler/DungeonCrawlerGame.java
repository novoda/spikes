package com.novoda.dungeoncrawler;

import java.util.Random;

public class DungeonCrawlerGame implements
    LoopObserver.AttackMonitor,
    LoopObserver.KillMonitor,
    LoopObserver.MovementMonitor,
    LoopObserver.DeathMonitor,
    FrameObserver.WinMonitor,
    FrameObserver.CompleteMonitor,
    FrameObserver.DrawCallback,
    FrameObserver.FrameCallback,
    FrameObserver.GameOverMonitor,
    TimeObserver.PauseMonitor,
    TimeObserver.ResumeMonitor {

    public interface HudDisplayer {
        void displayLives(int total);
    }

    private static final Display.CRGB PLAYER_COLOR = Display.CRGB.GREEN;

    private final int numLeds;
    private final ArduinoLoop looper;
    private final GameEngine gameEngine;
    private final Display display;
    private final HudDisplayer hud;
    private final SoundEffectsPlayer soundEffectsPlayer;

    public DungeonCrawlerGame(int numLeds,
                       GameEngine gameEngine,
                       Display display,
                       HudDisplayer hud,
                       SoundEffectsPlayer soundEffectsPlayer, ArduinoLoop looper) {
        this.numLeds = numLeds;
        this.gameEngine = gameEngine;
        this.display = display;
        this.hud = hud;
        this.soundEffectsPlayer = soundEffectsPlayer;
        this.looper = looper;
    }

    public void start() {
        looper.stop();
        looper.start(gameEngine::loop);
        gameEngine.loadLevel();
    }

    @Override
    public void onMove(int velocity) {
        soundEffectsPlayer.playMove(velocity);
    }

    @Override
    public void onAttack() {
        soundEffectsPlayer.playAttack();
    }

    @Override
    public void onKill() {
        soundEffectsPlayer.playKill();
    }

    @Override
    public void onDeath() {
        soundEffectsPlayer.playDie();
    }

    @Override
    public void onWin(long levelStartTime, long levelCurrentTime) {
        // LEVEL COMPLETE
        if (levelStartTime + 500 > levelCurrentTime) {
            int n = (int) Math.max(GameEngine.map((int) (levelCurrentTime - levelStartTime), 0, 500, numLeds, 0), 0);
            for (int i = numLeds - 1; i >= n; i--) {
                display.set(i, PLAYER_COLOR);
            }
        } else if (levelStartTime + 1000 > levelCurrentTime) {
            int n = (int) Math.max(GameEngine.map((int) (levelCurrentTime - levelStartTime), 500, 1000, numLeds, 0), 0);
            for (int i = 0; i < n; i++) {
                display.set(i, PLAYER_COLOR);
            }
        } else if (levelStartTime + 1200 > levelCurrentTime) {
            display.set(0, PLAYER_COLOR);
        }
    }

    @Override
    public void onGameComplete(long levelStartTime, long levelCurrentTime) {
        soundEffectsPlayer.playComplete();
        if (levelStartTime + 500 > levelCurrentTime) {
            int n = (int) Math.max(GameEngine.map((int) (levelCurrentTime - levelStartTime), 0, 500, numLeds, 0), 0);
            for (int i = numLeds - 1; i >= n; i--) {
                int brightness = (int) ((Math.sin(((i * 10) + levelCurrentTime) / 500.0) + 1) * 255);
                display.modifyHSV(i, brightness, 255, 50);
            }
        } else if (levelStartTime + 5000 > levelCurrentTime) {
            for (int i = numLeds - 1; i >= 0; i--) {
                int brightness = (int) ((Math.sin(((i * 10) + levelCurrentTime) / 500.0) + 1) * 255);
                display.modifyHSV(i, brightness, 255, 50);
            }
        } else if (levelStartTime + 5500 > levelCurrentTime) {
            int n = (int) Math.max(GameEngine.map((int) (levelCurrentTime - levelStartTime), 5000, 5500, numLeds, 0), 0);
            for (int i = 0; i < n; i++) {
                int brightness = (int) ((Math.sin(((i * 10) + levelCurrentTime) / 500.0) + 1) * 255);
                display.modifyHSV(i, brightness, 255, 50);
            }
        }
    }

    @Override
    public void onGameOver() {
        // TODO do something
    }

    @Override
    public void onFrameStart() {
        display.clear();
    }

    @Override
    public void onFrameEnd() {
        display.show();
    }

    @Override
    public void drawPlayer(int position) {
        int p = getLED(position);
        display.set(p, PLAYER_COLOR);
    }

    private int getLED(int pos) {
        // The world is 1000 pixels wide, this converts world units into an LED number
        return constrain((int) GameEngine.map(pos, 0, 1000, 0, numLeds - 1), numLeds - 1);
    }

    private int constrain(int value, int upper) {
        return Math.max(Math.min(value, upper), 0);
    }

    @Override
    public void drawConveyor(int startPoint, int endPoint, Direction direction, long frame) {
        int startPosition = getLED(startPoint);
        int endPosition = getLED(endPoint);
        for (int led = startPosition; led < endPosition; led++) {
            int n;
            if (direction == Direction.LEFT_TO_RIGHT) {
                n = (int) ((led + (frame / 100)) % 5);
            } else {
                n = (int) ((-led + (frame / 100)) % 5);
            }
            int blue = (int) ((5 - n) / 2.0);
            if (blue > 0) {
                display.set(led, new Display.CRGB(0, 0, blue));
            }
        }
    }

    @Override
    public void drawAttack(int startPoint, int centerPoint, int endPoint, int attackPower) {
        for (int i = getLED(endPoint) + 1; i <= getLED(startPoint) - 1; i++) {
            display.set(i, new Display.CRGB(0, 0, attackPower));
        }
        int i = getLED(centerPoint);
        if (attackPower > 90) {
            attackPower = 255;
            display.set(i, Display.CRGB.WHITE);
        } else {
            attackPower = 0;
            display.set(i, Display.CRGB.GREEN);
        }
        display.set(getLED(endPoint), new Display.CRGB(attackPower, attackPower, 255));
        display.set(getLED(startPoint), new Display.CRGB(attackPower, attackPower, 255));
    }

    @Override
    public void drawParticle(int position, int power) {
        display.set(getLED(position), new Display.CRGB(power, 0, 0));
    }

    @Override
    public void drawEnemy(int position) {
        int p = getLED(position);
        display.set(p, Display.CRGB.RED);
    }

    @Override
    public void drawLava(int lavaStartPosition, int lavaEndPosition, boolean enabled) {
        int A = getLED(lavaStartPosition);
        int B = getLED(lavaEndPosition);
        int flicker = new Random().nextInt(5);
        if (enabled) {
            for (int p = A; p <= B; p++) {
                display.set(p, new Display.CRGB(150 + flicker, 100 + flicker, 0));
            }
        } else {
            for (int p = A; p <= B; p++) {
                display.set(p, new Display.CRGB(3 + flicker, (int) ((3 + flicker) / 1.5), 0));
            }
        }
    }

    @Override
    public void drawBoss(int startPosition, int endPosition) {
        for (int i = getLED(startPosition); i <= getLED(endPosition); i++) {
            display.set(i, Display.CRGB.DARK_RED);
            // display.modifyMod(i, 100); // TODO: Investigate this! It prevents the boss from drawing
        }
    }

    @Override
    public void drawExit() {
        int exitPosition = numLeds - 1;
        display.set(exitPosition, new Display.CRGB(0, 0, 255));
    }

    @Override
    public void drawLives(int lives) {
        hud.displayLives(lives);
    }

    public void stop() {
        looper.stop();
    }

    @Override
    public void pause() {
        // no-op - Will implement this later
    }

    @Override
    public void resume() {
        // no-op - Will implement this later
    }

}
