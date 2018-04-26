package com.novoda.dungeoncrawler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int NUM_OF_SQUARES = 25;

    private DungeonCrawlerGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = new AndroidDeviceDisplay(this, findViewById(R.id.scrollView), NUM_OF_SQUARES);

        Screensaver screensaver = new Screensaver(display, NUM_OF_SQUARES);
        JoystickActuator joystickActuator = new AndroidViewJoystickActuator(findViewById(R.id.joystick));

        GameEngine gameEngine = new GameEngine(
                () -> game.onAttack(),
                () -> game.onKill(),
                v -> game.onMove(v),
                () -> game.onDeath(),
                (l, l2) -> game.onWin(l, l2),
                screensaver::draw,
                (s, c) -> game.onGameComplete(s, c),
                () -> game.onGameOver(),
                new GameEngine.DrawCallback() {
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
                joystickActuator
        );
        LogcatSoundEffectsPlayer soundEffectsPlayer = new LogcatSoundEffectsPlayer();
        ArduinoLoop looper = new ArduinoLoop();
        game = new DungeonCrawlerGame(NUM_OF_SQUARES,
                                      gameEngine,
                                      display,
                                      this::updateLives,
                                      soundEffectsPlayer, looper
        );

        findViewById(R.id.button2).setOnClickListener(v -> {
            game.start();
            Log.d("TUT", "Game restarting");
        });

        game.start();
        Log.d("TUT", "Game starting");
    }

    private void updateLives(int lives) {
        runOnUiThread(() -> ((TextView) findViewById(R.id.lives_text_view)).setText("Lives " + lives));
    }

    @Override
    protected void onDestroy() {
        game.stop();
        super.onDestroy();
    }

}
