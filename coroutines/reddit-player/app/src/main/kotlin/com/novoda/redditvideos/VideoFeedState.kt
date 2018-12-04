package com.novoda.redditvideos

import com.novoda.redditvideos.model.Video
import java.lang.Exception

sealed class VideoFeedState {

    object Loading : VideoFeedState(), InFlight
    data class LoadingWithCache(override val videos: List<Video>) : VideoFeedState(), HasVideos, InFlight
    data class Idle(override val videos: List<Video>) : VideoFeedState(), HasVideos
    data class Failure(override val exception: Exception) : VideoFeedState(), HasFailure
    data class FailureWithCache(
        override val videos: List<Video>,
        override val exception: Exception
    ) : VideoFeedState(), HasVideos, HasFailure

    interface HasVideos {
        val videos: List<Video>
    }

    interface HasFailure {
        val exception: Exception
    }

    interface InFlight

}

