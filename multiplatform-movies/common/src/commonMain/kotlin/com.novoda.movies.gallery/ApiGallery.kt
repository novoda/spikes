package com.novoda.movies.gallery

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiGallery(@SerialName("results") val apiMoviePosters: List<ApiMoviePoster>)

@Serializable
data class ApiMoviePoster(
        @SerialName("id") val movieId: Long,
        @SerialName("poster_path") val thumbnailUrl: String
)
