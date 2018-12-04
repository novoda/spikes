package com.novoda.redditvideos.model

import com.novoda.reddit.data.Listing
import com.novoda.reddit.data.ListingService
import com.novoda.reddit.data.Thing
import retrofit2.Response

fun ListingService.findRemoveVideos(): Result<List<Video>> = try {
    videos().execute().run {
        when {
            isSuccessful -> Result.Success(posts.map { it.toVideoEntry() })
            else -> Result.Failure(NetworkError(code(), message()))
        }
    }
} catch (e: Exception) {
    Result.Failure(e)
}

fun VideoDao.findLocalVideos(): Result<List<Video>> = try {
    Result.Success(getAll().map { it.toVideoEntry() })
} catch (e: Exception) {
    Result.Failure(e)
}

fun VideoDao.replaceCache(videos: List<Video>) {
    clear()
    insertAll(videos.map { it.toVideoEntity() })
}

data class NetworkError(val code: Int, val errorMessage: String) : RuntimeException(errorMessage)

sealed class Result<out T> {
    object Pending : Result<Nothing>()
    data class Success<T>(val success: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}

private val Response<Listing<Thing.Post>>.posts get() = body()?.children ?: emptyList()

private fun Thing.Post.toVideoEntry() = Video(
    title = title,
    thumbnail = Thumbnail(
        preview.images.getOrNull(0)?.source?.url?.decode() ?: "No image"
    ),
    id = name
)

private fun VideoEntity.toVideoEntry() = Video(
    title = "[restored]$title",
    thumbnail = Thumbnail(thumbnail),
    id = id
)

private fun Video.toVideoEntity() = VideoEntity(
    id = id,
    title = title,
    thumbnail = thumbnail.value
)

private fun String.decode() = replace("&amp;", "&")
