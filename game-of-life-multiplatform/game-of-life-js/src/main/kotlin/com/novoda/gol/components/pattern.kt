@file:Suppress("UnsafeCastFromDynamic")

package com.novoda.gol.components

import com.novoda.gol.patterns.PatternEntity
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import react.RBuilder
import react.dom.div
import react.dom.img

fun RBuilder.pattern(patternEntity: PatternEntity, onPatternSelected: () -> Unit) = div {

    attrs.style = kotlinext.js.js {
        padding = "10px"
    }

    div {
        +patternEntity.getName()
    }

    for (y in 0 until patternEntity.getHeight()) {
        div {
            attrs.style = kotlinext.js.js {
                display = "flex"
            }

            for (x in 0 until patternEntity.getWidth()) {
                val cell = patternEntity.cellAtPosition(x, y)
                img {

                    attrs.style = kotlinext.js.js {
                        width = "10px"
                        height = "10px"
                        background = if (cell.isAlive) "#000" else "#AAA"
                    }

                    attrs.onClickFunction = {
                        onPatternSelected.invoke()
                    }

                }
            }
        }
    }
}