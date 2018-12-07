//package com.novoda.redditvideos
//
//import androidx.paging.PagedList
//
//internal sealed class VideoFeedState {
//
//    object Loading : VideoFeedState(), InFlight
//    data class LoadingWithCache(override val videos: PagedList<VideoFeedAdapter.Item>) : VideoFeedState(), HasVideos,
//        InFlight
//    data class Idle(override val videos: PagedList<VideoFeedAdapter.Item>) : VideoFeedState(), HasVideos
//    data class Failure(override val exception: Exception) : VideoFeedState(), HasFailure
//    data class FailureWithCache(
//        override val videos: PagedList<VideoFeedAdapter.Item>,
//        override val exception: Exception
//    ) : VideoFeedState(), HasVideos, HasFailure
//
//    interface HasVideos {
//        val videos: PagedList<VideoFeedAdapter.Item>
//    }
//
//    interface HasFailure {
//        val exception: Exception
//    }
//
//    interface InFlight
//
//}
//
