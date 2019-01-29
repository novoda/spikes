package com.novoda.movies.gallery

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class GalleryPresenter internal constructor(private val uiContext: CoroutineContext, private val galleryFetcher: GalleryFetcher) : CoroutineScope {

    private lateinit var job: Job
    private lateinit var view: View

    override val coroutineContext: CoroutineContext
        get() = uiContext + job + CoroutineExceptionHandler { _, throwable ->
            job = Job()
            view.renderError(throwable.message)
        }


    fun startPresenting(view: View) {
        this.view = view
        this.job = Job()

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
        fun renderError(message: String?)
    }
}
