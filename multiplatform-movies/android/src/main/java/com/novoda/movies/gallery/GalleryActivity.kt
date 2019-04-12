package com.novoda.movies.gallery

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.widget.Toast
import com.novoda.movies.R
import kotlinx.android.synthetic.main.activity_gallery.*

private const val NO_OF_COLUMNS = 3

class GalleryActivity : AppCompatActivity(), GalleryPresenter.View {

    private val presenter = GalleryDependencyProvider().providerPresenter()

    private val galleryAdapter: GalleryAdapter by lazy {
        GalleryAdapter(moviePosterWidthInPixels())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        movie_collection.adapter = galleryAdapter
        movie_collection.layoutManager = GridLayoutManager(this, NO_OF_COLUMNS)
    }

    override fun onStart() {
        super.onStart()
        presenter.startPresenting(this)
    }

    override fun onStop() {
        presenter.stopPresenting()
        super.onStop()
    }

    override fun renderError(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun render(gallery: Gallery) {
        galleryAdapter.updateWith(gallery)
    }

    private fun moviePosterWidthInPixels(): Int = resources.displayMetrics.widthPixels / NO_OF_COLUMNS
}
