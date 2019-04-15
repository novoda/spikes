package com.novoda.movies.gallery

import kotlinx.css.*
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import react.setState
import styled.*

interface AppState : RState {
    var gallery: Gallery?
    var message: String?
}

private class App : RComponent<RProps, AppState>(), GalleryPresenter.View {
    override fun render(gallery: Gallery) {
        setState {
            this.gallery = gallery
            message = null
        }
    }

    override fun renderError(message: String?) {
        setState {
            this.message = message
        }
    }

    val presenter = GalleryDependencyProvider().providerPresenter()

    override fun componentWillMount() {
        presenter.startPresenting(this)
    }

    override fun componentWillUnmount() {
        presenter.stopPresenting()
    }

    override fun RBuilder.render() {
        topAppBar("Movies")
        state.gallery?.let {
            styledDiv {
                css {
                    classes = mutableListOf("mdc-top-app-bar--fixed-adjust")
                    display = Display.grid
                    gridTemplateColumns = GridTemplateColumns("repeat(auto-fill, minmax(200px, 1fr))")
                }
                it.moviePosters.forEach { poster ->
                    styledA {
                        css {
                        }
                        styledImg(src = poster.thumbnailUrl) {
                            css {
                                width = 100.pct
                                height = 100.pct
                            }
                        }
                    }
                }
            }
        }
        state.message?.let { h2 { +it } }
    }
}

fun RBuilder.app() = child(App::class) {
}
