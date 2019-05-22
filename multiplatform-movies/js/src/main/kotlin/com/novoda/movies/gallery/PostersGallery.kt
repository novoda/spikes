package com.novoda.movies.gallery

import kotlinx.css.Display
import kotlinx.css.GridTemplateColumns
import kotlinx.css.pct
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.header
import react.dom.section
import react.dom.span
import styled.css
import styled.styledA
import styled.styledDiv
import styled.styledImg

interface PostersGalleryProps : RProps {
    var gallery: Gallery
}

private class PostersGallery : RComponent<PostersGalleryProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.grid
                gridTemplateColumns = GridTemplateColumns("repeat(auto-fill, minmax(200px, 1fr))")
            }
            props.gallery.moviePosters.forEach { poster ->
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

fun RBuilder.postersGallery(gallery: Gallery) = child(PostersGallery::class) {
    attrs.gallery = gallery
}
