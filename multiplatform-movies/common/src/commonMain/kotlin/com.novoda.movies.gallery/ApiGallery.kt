package com.novoda.movies.gallery

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiGallery(@SerialName("results") val apiMoviePosters: List<ApiMoviePoster>)

@Serializable
internal data class ApiMoviePoster(
        @SerialName("id") val movieId: Long,
        @SerialName("poster_path") val thumbnailUrl: String
)
