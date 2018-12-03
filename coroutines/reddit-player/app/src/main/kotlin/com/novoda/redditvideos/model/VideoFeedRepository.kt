package com.novoda.redditvideos.model

import com.novoda.reddit.data.Listing
import com.novoda.reddit.data.ListingService
import com.novoda.reddit.data.Thing
import retrofit2.Response

fun networkFetch(listing: ListingService): Result<List<VideoEntry>> = try {
    val response: Response<Listing<Thing.Post>> = listing.videos().execute()
    when {
        response.isSuccessful -> Result.Success(response.posts.map { it.toVideoEntry() })
        else -> Result.Failure(NetworkError(response.code(), response.message()))
    }
} catch (e: Exception) {
    Result.Failure(e)
}

data class NetworkError(val code: Int, val errorMessage: String) : RuntimeException(errorMessage)

sealed class Result<out T> {
    object Pending : Result<Nothing>()
    data class Success<T>(val value: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}

private val Response<Listing<Thing.Post>>.posts get() = body()?.children ?: emptyList()

private fun Thing.Post.toVideoEntry() = VideoEntry(
    title = title,
    thumbnail = Thumbnail(
        preview.images.getOrNull(0)?.source?.url?.decode() ?: "No image"
    )
)

private fun String.decode() = replace("&amp;", "&")
