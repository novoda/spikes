package com.novoda.movies.gallery

import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.h2
import react.setState

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
        state.gallery?.let { h2 { "${it.moviePosters.size} movies" } }
        state.message?.let { h2 { +it } }
    }
}

fun RBuilder.app() = child(App::class) {
}
