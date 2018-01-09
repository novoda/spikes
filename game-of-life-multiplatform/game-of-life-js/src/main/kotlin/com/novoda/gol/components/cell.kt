package com.novoda.gol.components

import com.novoda.gol.CellEntity
import kotlinext.js.js
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import react.*
import react.dom.img

@Suppress("UnsafeCastFromDynamic")

fun RBuilder.cell(cellEntity: CellEntity, onClick: () -> Unit) = img {

    attrs.style = js {
        width = "10px"
        height = "10px"
        background = if (cellEntity.isAlive) "#000" else "#AAA"
    }

    attrs.onClickFunction = {
        onClick.invoke()
    }

}

