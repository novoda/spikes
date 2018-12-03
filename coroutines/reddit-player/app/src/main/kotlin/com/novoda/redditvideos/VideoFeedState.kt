package com.novoda.redditvideos

import com.novoda.redditvideos.model.VideoEntry

sealed class VideoFeedState {

    object Loading : VideoFeedState()
    data class Idle(override val videos: List<VideoEntry>) : VideoFeedState(), HasVideos
    data class Failure(val message: String) : VideoFeedState()

    interface HasVideos {

        val videos: List<VideoEntry>

    }

}

