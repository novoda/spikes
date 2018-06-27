package com.novoda.dungeoncrawlercompanion

import com.novoda.dungeoncrawler.ArduinoLoop
import com.novoda.dungeoncrawler.DefaultStartClock
import com.novoda.dungeoncrawler.Direction
import com.novoda.dungeoncrawler.Display
import com.novoda.dungeoncrawler.DungeonCrawlerGame
import com.novoda.dungeoncrawler.DungeonCrawlerGame.HudDisplayer
import com.novoda.dungeoncrawler.FrameObserver
import com.novoda.dungeoncrawler.Redux
import com.yheriatovych.reductor.Store

object InitHack {

    private val store = Store.create(ReplayReducer(), Redux.GameState.getInitialState())

    fun newInstance(numOfSquares: Int,
                    display: Display,
                    hudDisplayer: HudDisplayer,
                    looper: ArduinoLoop): DungeonCrawlerGame {
        val clock = DefaultStartClock()
        val gameEngine = ReplayGameEngine(
                ReplayFetcher(),
                store,
                clock
        )
        val game = DungeonCrawlerGame(
                numOfSquares,
                gameEngine,
                display,
                hudDisplayer,
                NoOpSoundEffectsPlayer(),
                looper
        )

        FrameObserver(
                store,
                FrameObserver.NoInputMonitor { },
                FrameObserver.WinMonitor { l, l2 -> Unit },
                FrameObserver.CompleteMonitor { s, c -> Unit },
                FrameObserver.GameOverMonitor { Unit },
                createDrawCallback(game),
                createFrameCallback(game),
                clock
        )

        return game
    }

    private fun createDrawCallback(game: DungeonCrawlerGame): FrameObserver.DrawCallback {
        return object : FrameObserver.DrawCallback {

            override fun drawPlayer(position: Int) {
                game.drawPlayer(position)
            }

            override fun drawConveyor(startPoint: Int, endPoint: Int, direction: Direction, frame: Long) {
                game.drawConveyor(startPoint, endPoint, direction, frame)
            }

            override fun drawAttack(startPoint: Int, centerPoint: Int, endPoint: Int, attackPower: Int) {
                game.drawAttack(startPoint, centerPoint, endPoint, attackPower)
            }

            override fun drawParticle(position: Int, power: Int) {
                game.drawParticle(position, power)
            }

            override fun drawEnemy(position: Int) {
                game.drawEnemy(position)
            }

            override fun drawLava(startPosition: Int, endPosition: Int, enabled: Boolean) {
                game.drawLava(startPosition, endPosition, enabled)
            }

            override fun drawBoss(startPosition: Int, endPosition: Int) {
                game.drawBoss(startPosition, endPosition)
            }

            override fun drawExit() {
                game.drawExit()
            }

            override fun drawLives(lives: Int) {
                game.drawLives(lives)
            }

        }
    }

    private fun createFrameCallback(game: DungeonCrawlerGame): FrameObserver.FrameCallback {
        return object : FrameObserver.FrameCallback {
            override fun onFrameStart() {
                                        game.onFrameStart()
            }

            override fun onFrameEnd() {
                game.onFrameEnd()
            }
        }
    }

}
