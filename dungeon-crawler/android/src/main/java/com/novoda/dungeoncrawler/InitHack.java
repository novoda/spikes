package com.novoda.dungeoncrawler;

import com.novoda.dungeoncrawler.DungeonCrawlerGame.HudDisplayer;
import com.yheriatovych.reductor.Store;

public class InitHack {

    private static DungeonCrawlerGame game;
    private static final Store<Redux.GameState> store = Store.create(new Redux.GameReducer(), Redux.GameState.getInitialState(), new FirebaseGameStateLogger());

    public static DungeonCrawlerGame newInstance(int numOfSquares,
                                                 Display display,
                                                 HudDisplayer hudDisplayer,
                                                 JoystickActuator joystickActuator,
                                                 SoundEffectsPlayer soundEffectsPlayer,
                                                 Screensaver screensaver, ArduinoLoop looper) {
        StartClock clock = new PausableStartClock(new DefaultStartClock());
        new LoopObserver(
            store,
            joystickActuator,
            () -> game.onAttack(),
            v -> game.onMove(v),
            () -> game.onDeath(),
            () -> game.onKill()
        );
        new FrameObserver(
            store,
            screensaver::draw,
            (l, l2) -> game.onWin(l, l2),
            (s, c) -> game.onGameComplete(s, c),
            () -> game.onGameOver(),
            new FrameObserver.DrawCallback() {

                @Override
                public void drawPlayer(int position) {
                    game.drawPlayer(position);
                }

                @Override
                public void drawConveyor(int startPoint, int endPoint, Direction direction, long frame) {
                    game.drawConveyor(startPoint, endPoint, direction, frame);
                }

                @Override
                public void drawAttack(int startPoint, int centerPoint, int endPoint, int attackPower) {
                    game.drawAttack(startPoint, centerPoint, endPoint, attackPower);
                }

                @Override
                public void drawParticle(int position, int power) {
                    game.drawParticle(position, power);
                }

                @Override
                public void drawEnemy(int position) {
                    game.drawEnemy(position);
                }

                @Override
                public void drawLava(int startPosition, int endPosition, boolean enabled) {
                    game.drawLava(startPosition, endPosition, enabled);
                }

                @Override
                public void drawBoss(int startPosition, int endPosition) {
                    game.drawBoss(startPosition, endPosition);
                }

                @Override
                public void drawExit() {
                    game.drawExit();
                }

                @Override
                public void drawLives(int lives) {
                    game.drawLives(lives);
                }

            },
            new FrameObserver.FrameCallback() {
                @Override
                public void onFrameStart() {
                    game.onFrameStart();
                }

                @Override
                public void onFrameEnd() {
                    game.onFrameEnd();
                }
            },
            clock
        );
        new TimeObserver(store, clock);
        GameEngine gameEngine = new PauseResumeGameEngine(
            store,
            joystickActuator,
            clock
        );
        game = new DungeonCrawlerGame(
            numOfSquares,
            gameEngine,
            display,
            hudDisplayer,
            soundEffectsPlayer,
            looper
        );
        return game;
    }

}
