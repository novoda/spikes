package com.novoda.movies.gallery

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class GalleryPresenter internal constructor(private val uiContext: CoroutineContext, private val galleryFetcher: GalleryFetcher) : CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = uiContext + job


    fun startPresenting(view: View) {
        job = Job()

        launch {
            val gallery = galleryFetcher.fetchGallery()
            view.render(gallery)
        }
    }

    fun stopPresenting() {
        job.cancel()
    }

    interface View {
        fun render(gallery: Gallery)
        fun renderError(message: String)
    }
}
