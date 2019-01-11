package com.novoda.movies.gallery

interface GalleryBackend {
    suspend fun popularMoviesGallery(): ApiGallery
}
