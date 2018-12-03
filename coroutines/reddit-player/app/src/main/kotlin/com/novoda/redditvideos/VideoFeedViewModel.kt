package com.novoda.redditvideos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.novoda.reddit.data.ListingService
import com.novoda.redditvideos.VideoFeedState.*
import com.novoda.redditvideos.model.*
import com.novoda.redditvideos.support.functional.andThen
import com.novoda.redditvideos.support.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job

class VideoFeedViewModel @JvmOverloads constructor(
    app: Application,
    videoDao: VideoDao = app.database.videoDao(),
    listingService: ListingService = app.apiClient.listingService
) : AndroidViewModel(app), CoroutineScope {

    private val job = Job()
    override val coroutineContext = GlobalScope.coroutineContext + job

    private val refreshCacheOnSuccess = { result: Result<List<Video>> ->
        if (result is Result.Success) videoDao.replaceCache(result.value)
    }

    private val data = CombinedLiveData(
        initialValue = Result.Pending,
        job = job,
        getLocal = videoDao::findLocalVideos,
        getRemote = listingService::findRemoveVideos.andThen(refreshCacheOnSuccess)
    ).also { it.load() }

    val state: LiveData<VideoFeedState> = data.map { result ->
        when (result) {
            is Result.Pending -> Loading
            is Result.Success -> Idle(videos = result.value)
            is Result.Failure -> Failure(result.exception.message ?: "Unknown error", result.exception)
        }
    }

    fun reload() {
        data.reload()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}

