package com.novoda.movies.gallery

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w1280"

internal class GalleryFetcher(private val galleryBackend: GalleryBackend) {

    suspend fun fetchGallery(): Gallery {
        val apiGallery = galleryBackend.popularMoviesGallery()
        return Gallery(toGallery(apiGallery))
    }

    private fun toGallery(apiGallery: ApiGallery): List<MoviePoster> {
        return apiGallery.apiMoviePosters.map {
            MoviePoster(it.movieId, "$IMAGE_BASE_URL${it.thumbnailUrl}")
        }
    }
}
