package com.novoda.movies.gallery

import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.header
import react.dom.section
import react.dom.span

interface TopAppBarProps: RProps {
    var title: String
}

private class TopAppBar: RComponent<TopAppBarProps, RState>() {
    override fun RBuilder.render() {
        header(classes = "mdc-top-app-bar mdc-top-app-bar--fixed") {
            div(classes = "mdc-top-app-bar__row") {
                section(classes = "mdc-top-app-bar__section mdc-top-app-bar__section--align-start") {
                    span(classes = "mdc-top-app-bar__title") { +props.title }
                }
            }
        }
    }
}

fun RBuilder.topAppBar(title: String) = child(TopAppBar::class) {
    attrs.title = title
}
