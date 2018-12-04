package com.novoda.redditvideos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.novoda.reddit.data.ListingService
import com.novoda.redditvideos.model.*
import com.novoda.redditvideos.support.functional.andThen
import kotlinx.coroutines.*

class VideoFeedViewModel @JvmOverloads constructor(
    app: Application,
    videoDao: VideoDao = app.database.videoDao(),
    listingService: ListingService = app.apiClient.listingService
) : AndroidViewModel(app), CoroutineScope {

    private val job = Job()
    override val coroutineContext = GlobalScope.coroutineContext + job

    private val refreshCacheOnSuccess = { result: Result<List<Video>> ->
        if (result is Result.Success) videoDao.replaceCache(result.success)
    }

    private val data = VideoLiveData(
        job = job,
        getLocal = videoDao::findLocalVideos,
        getRemote = listingService::findRemoveVideos.andThen(refreshCacheOnSuccess)
    ).also { it.load() }

    val state: LiveData<VideoFeedState> = data

    fun reload() {
        data.reload()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}

private class VideoLiveData(
    job: Job,
    val getLocal: () -> Result<List<Video>>,
    val getRemote: () -> Result<List<Video>>
) : LiveData<VideoFeedState>(), CoroutineScope {

    override val coroutineContext = GlobalScope.coroutineContext + job

    init {
        value = VideoFeedState.Loading
    }

    fun reload() = launch(Dispatchers.Main) {
        value = when(val state = value) {
            is VideoFeedState.HasVideos -> VideoFeedState.LoadingWithCache(
                state.videos
            )
            else -> VideoFeedState.Loading
        }
        value = async(Dispatchers.IO) { getRemote().toState(VideoFeedState::Idle) }.await()
    }

    fun load() = launch(Dispatchers.Main) {
        value = async(Dispatchers.IO) { getLocal().toState(VideoFeedState::LoadingWithCache) }.await()
        value = async(Dispatchers.IO) { getRemote().toState(VideoFeedState::Idle) }.await()
    }

    private fun Result<List<Video>>.toState(onSuccess: (List<Video>) -> VideoFeedState) = when(this) {
        is Result.Pending -> VideoFeedState.Loading
        is Result.Success -> onSuccess(success)
        is Result.Failure -> VideoFeedState.Failure(
            exception.message ?: "Unknown", exception
        )
    }

}
