@file:Suppress("UnsafeCastFromDynamic")

package com.novoda.gol.components

import com.novoda.gol.data.ListBasedMatrix
import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.patterns.PatternRepository
import kotlinx.html.style
import react.*
import react.dom.div
import react.dom.h2
import kotlin.browser.window

class App : RComponent<RProps, State>() {

    override fun State.init() {
        patternEntities = PatternRepository.patterns()
    }

    override fun RBuilder.render(): ReactElement? =
            div {
                attrs.style = kotlinext.js.js {
                    display = "flex"
                    flexDirection = "column"
                }

                controlButton(controlButtonLabel(), {
                    if (state.gameLoop != null) {
                        stopSimulation()
                    } else {
                        startSimulation()
                    }
                })

                div {
                    attrs.style = kotlinext.js.js {
                        display = "flex"
                    }

                    board(isIdle(), state.selectedPattern)

                    if (isIdle()) {

                        div {

                            attrs.style = kotlinext.js.js {
                                padding = "10px"
                            }

                            h2 { +"Choose a pattern" }

                            for (pattern in state.patternEntities) {
                                pattern(pattern, {
                                    ListBasedMatrix(width = 50, height = 50, seeds = pattern.getSeeds())
                                    setState {
                                        selectedPattern = pattern
                                    }
                                })
                            }
                        }
                    }
                }
            }

    private fun controlButtonLabel() = if (isIdle()) "Start simulation" else "Stop Simulation"

    private fun isIdle() = state.gameLoop == null

    private fun startSimulation() {
        setState {
            gameLoop = window.setInterval({
                nextIteration()
            }, 300)
        }
    }

    private fun stopSimulation() {
        window.clearInterval(state.gameLoop!!)

        setState {
            gameLoop = null
        }
    }

    private fun nextIteration() {
        setState {}
    }

}

interface State : RState {
    var patternEntities: List<PatternEntity>
    var gameLoop: Int?
    var selectedPattern: PatternEntity?
}

fun RBuilder.app() = child(App::class) {}

