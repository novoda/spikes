@file:Suppress("UnsafeCastFromDynamic")

package com.novoda.gol.components

import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.patterns.PatternRepository
import kotlinx.html.style
import react.*
import react.dom.div
import react.dom.h2

class App : RComponent<RProps, State>() {

    override fun State.init() {
        patternEntities = PatternRepository.patterns()
        isIdle = true
    }

    override fun RBuilder.render(): ReactElement? =
            div {
                attrs.style = kotlinext.js.js {
                    display = "flex"
                    flexDirection = "column"
                }

                controlButton(controlButtonLabel(), {
                    setState {
                        isIdle = isIdle.not()
                    }
                })

                div {
                    attrs.style = kotlinext.js.js {
                        display = "flex"
                    }

                    board(state.isIdle, state.selectedPattern)

                    if (state.isIdle) {

                        div {

                            attrs.style = kotlinext.js.js {
                                padding = "10px"
                            }

                            h2 { +"Choose a pattern" }

                            for (patternEntity in state.patternEntities) {
                                pattern(patternEntity, {
                                    setState {
                                        selectedPattern = patternEntity
                                    }
                                })
                            }
                        }
                    }
                }
            }

    private fun controlButtonLabel() = if (state.isIdle) "Start simulation" else "Stop Simulation"

}

interface State : RState {
    var patternEntities: List<PatternEntity>
    var isIdle: Boolean
    var selectedPattern: PatternEntity?
}

fun RBuilder.app() = child(App::class) {}

