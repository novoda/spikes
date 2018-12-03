package com.novoda.redditvideos

sealed class VideoFeedState {

    object Loading : VideoFeedState()
    data class Idle(override val videos: List<VideoEntry>) : VideoFeedState(), HasVideos
    data class Failure(val message: String) : VideoFeedState()

    interface HasVideos {

        val videos: List<VideoEntry>

    }

}

data class VideoEntry(
    val title: String,
    val thumbnail: Thumbnail
)

inline class Thumbnail(val value: String)
