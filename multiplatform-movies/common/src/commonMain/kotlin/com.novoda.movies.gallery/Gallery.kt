package com.novoda.movies.gallery

data class Gallery(val moviePosters: List<MoviePoster>)

data class MoviePoster(val movieId: Long, val thumbnailUrl: String)
