package com.novoda.movies.gallery

import io.ktor.http.Url

data class Gallery(val moviePosters: List<MoviePoster>)

data class MoviePoster(val movieId: Long, val thumbnailUrl: Url)
