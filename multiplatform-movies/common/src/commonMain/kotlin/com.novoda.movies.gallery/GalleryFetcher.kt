package com.novoda.movies.gallery

internal class GalleryFetcher(private val galleryBackend: GalleryBackend) {

    suspend fun fetchGallery(): Gallery {
        val apiGallery = galleryBackend.popularMoviesGallery()
        return Gallery(toGallery(apiGallery))
    }

    private fun toGallery(apiGallery: ApiGallery): List<MoviePoster> {
        return apiGallery.apiMoviePosters.map {
            MoviePoster(it.movieId, "https://image.tmdb.org/t/p/w1280${it.thumbnailUrl}")
        }
    }
}
