package com.novoda.movies.gallery

import react.*
import react.dom.div
import react.dom.h2

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
        div("mdc-top-app-bar--fixed-adjust") {
            state.gallery?.let(this::postersGallery)
            state.message?.let { h2 { +it } }
        }
    }
}

fun RBuilder.app() = child(App::class) {
}
