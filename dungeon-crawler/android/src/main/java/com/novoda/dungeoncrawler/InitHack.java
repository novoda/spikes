package com.novoda.dungeoncrawler;

import com.novoda.dungeoncrawler.DungeonCrawlerGame.HudDisplayer;
import com.yheriatovych.reductor.Store;

class InitHack {

    private static DungeonCrawlerGame game;
    private static final Store<Redux.GameState> store = Store.create(new Redux.GameReducer(), Redux.GameState.getInitialState(), new MiddlewareLogger());

    static DungeonCrawlerGame newInstance(int numOfSquares,
                                          Display display,
                                          HudDisplayer hudDisplayer,
                                          JoystickActuator joystickActuator,
                                          SoundEffectsPlayer soundEffectsPlayer,
                                          Screensaver screensaver, ArduinoLoop looper) {
        StartClock clock = new StartClock();
        new GameDrawer(
            store,
            screensaver::draw,
            (l, l2) -> game.onWin(l, l2),
            (s, c) -> game.onGameComplete(s, c),
            () -> game.onGameOver(),
            new GameDrawer.DrawCallback() {
                @Override
                public void startDraw() {
                    game.startDraw();
                }

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

                @Override
                public void finishDraw() {
                    game.finishDraw();
                }
            },
            clock
        );
        GameEngine gameEngine = new GameEngine(
            store,
            () -> game.onAttack(),
            v -> game.onMove(v),
            () -> game.onDeath(),
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
