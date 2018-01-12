package com.novoda.gol.components

import kotlinext.js.js
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import react.*
import react.dom.button

@Suppress("UnsafeCastFromDynamic")

fun RBuilder.controlButton(label: String, onClick: () -> Unit) = button {
    attrs.style = js {
        color = "#e34f26"
        width = "120px"
    }

    attrs.onClickFunction = {
        onClick.invoke()
    }

    +label
}
