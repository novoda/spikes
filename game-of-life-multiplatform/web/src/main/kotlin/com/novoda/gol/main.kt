package com.novoda.gol

import com.novoda.gol.components.app
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    window.onload = {

        render(document.getElementById("root")!!) {
            app()
        }

    }
}
