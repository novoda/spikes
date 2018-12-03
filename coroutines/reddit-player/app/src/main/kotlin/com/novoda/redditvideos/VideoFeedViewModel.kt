package com.novoda.redditvideos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.novoda.reddit.data.ApiAware
import com.novoda.redditvideos.VideoFeedState.*
import com.novoda.redditvideos.model.*
import com.novoda.redditvideos.support.functional.andThen
import com.novoda.redditvideos.support.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import retrofit2.Retrofit

class VideoFeedViewModel @JvmOverloads constructor(
    application: Application,
    override val retrofit: Retrofit = application.retrofit,
    override val database: AppDatabase = application.database
) : AndroidViewModel(application), CoroutineScope, ApiAware, DatabaseAware {

    private val job = Job()
    override val coroutineContext = GlobalScope.coroutineContext + job

    private val localVideos = { localFetch(videos) }
    private val networkVideos = { networkFetch(listing) }
    private val refreshCacheOnSuccess = { result: Result<List<Video>> ->
        if (result is Result.Success) videos.replaceCache(result.value)
    }

    private val data = CombinedLiveData(
        initialValue = Result.Pending,
        job = job,
        getFirst = localVideos,
        getSecond = networkVideos.andThen(refreshCacheOnSuccess)
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

