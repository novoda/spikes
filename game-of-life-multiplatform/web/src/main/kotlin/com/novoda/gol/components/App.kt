@file:Suppress("UnsafeCastFromDynamic")

package com.novoda.gol.components

import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.patterns.PatternRepository
import com.novoda.gol.presentation.AppPresenter
import com.novoda.gol.presentation.AppView
import com.novoda.gol.presentation.BoardViewInput
import com.novoda.gol.presentation.PatternViewState
import kotlinx.html.style
import react.*
import react.dom.div
import react.dom.h2

class App : RComponent<RProps, State>(), AppView {

    override var onControlButtonClicked: () -> Unit = {}
    override var onPatternSelected: (pattern: PatternEntity) -> Unit = {}

    private val presenter: AppPresenter = AppPresenter()

    override fun componentWillMount() {
        presenter.bind(this)
    }

    override fun componentWillUnmount() {
        presenter.unbind(this)
    }

    override fun State.init() {
        patternViewState = PatternViewState(true, PatternRepository.patterns())
    }

    override fun renderControlButtonLabel(controlButtonLabel: String) {
        setState {
            this.controlButtonLabel = controlButtonLabel
        }
    }

    override fun renderPatternSelectionVisibility(visibility: Boolean) {
        setState {
            patternViewState.shouldDisplay = visibility
        }
    }

    override fun renderBoardWith(boardViewInput: BoardViewInput) {
        setState {
            this.boardViewInput = boardViewInput
        }
    }

    override fun RBuilder.render(): ReactElement? =
            div {
                attrs.style = kotlinext.js.js {
                    display = "flex"
                    flexDirection = "column"
                }

                controlButton(state.controlButtonLabel, {
                    onControlButtonClicked()
                })

                div {
                    attrs.style = kotlinext.js.js {
                        display = "flex"
                    }

                    board(state.boardViewInput)

                    if (state.patternViewState.shouldDisplay) {

                        div {

                            attrs.style = kotlinext.js.js {
                                padding = "10px"
                            }

                            h2 { +"Choose a pattern" }

                            state.patternViewState.patternEntities.forEach { pattern ->
                                pattern(pattern, {
                                    onPatternSelected(pattern)
                                })
                            }
                        }
                    }
                }
            }
}

interface State : RState {
    var patternViewState: PatternViewState
    var boardViewInput: BoardViewInput
    var controlButtonLabel: String
}

fun RBuilder.app() = child(App::class) {}

