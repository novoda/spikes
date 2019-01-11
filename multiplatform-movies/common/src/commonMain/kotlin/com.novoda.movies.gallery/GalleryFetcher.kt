package com.novoda.movies.gallery

import io.ktor.http.Url

class GalleryFetcher(private val galleryBackend: GalleryBackend) {

    suspend fun fetchGallery(): Gallery {
        val apiGallery = galleryBackend.popularMoviesGallery()
        return Gallery(toGallery(apiGallery))
    }

    private fun toGallery(apiGallery: ApiGallery): List<MoviePoster> {
        return apiGallery.apiMoviePosters.map {
            MoviePoster(it.movieId, Url(it.thumbnailUrl))
        }
    }
}
