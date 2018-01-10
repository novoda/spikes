@file:Suppress("UnsafeCastFromDynamic")

package com.novoda.gol.components

import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.patterns.PatternRepository
import com.novoda.gol.presentation.AppPresenter
import com.novoda.gol.presentation.AppView
import kotlinx.html.style
import react.*
import react.dom.div
import react.dom.h2
import kotlin.properties.Delegates.observable

class App : RComponent<RProps, State>(), AppView {

    override var onControlButtonClicked: () -> Unit = {}

    override var controlButtonLabel by observable("") { _, _, newValue ->
        setState {
            controlButtonLabel = newValue
        }
    }

    private val presenter: AppPresenter = AppPresenter()

    override fun componentWillMount() {
        presenter.bind(this)
    }

    override fun componentWillUnmount() {
        presenter.unbind(this)
    }

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

                controlButton(state.controlButtonLabel, {
                    onControlButtonClicked()
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
}

interface State : RState {
    var patternEntities: List<PatternEntity>
    var isIdle: Boolean
    var selectedPattern: PatternEntity?
    var controlButtonLabel: String
}

fun RBuilder.app() = child(App::class) {}

