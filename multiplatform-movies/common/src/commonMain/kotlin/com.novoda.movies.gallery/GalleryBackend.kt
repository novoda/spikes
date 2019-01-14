package com.novoda.movies.gallery

internal interface GalleryBackend {
    suspend fun popularMoviesGallery(): ApiGallery
}
