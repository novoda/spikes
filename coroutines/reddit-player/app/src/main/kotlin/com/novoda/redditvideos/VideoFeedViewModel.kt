package com.novoda.redditvideos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.novoda.reddit.data.ApiAware
import com.novoda.redditvideos.VideoFeedState.*
import com.novoda.redditvideos.model.CombinedLiveData
import com.novoda.redditvideos.model.Result
import com.novoda.redditvideos.model.networkFetch
import com.novoda.redditvideos.support.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import retrofit2.Retrofit

class VideoFeedViewModel @JvmOverloads constructor(
    application: Application,
    override val retrofit: Retrofit = application.retrofit
) : AndroidViewModel(application), CoroutineScope, ApiAware {

    private val job = Job()

    override val coroutineContext = GlobalScope.coroutineContext + job

    private val data = CombinedLiveData(Result.Pending, job, { networkFetch(listing) })
        .also { it.load() }

    val state: LiveData<VideoFeedState> = data.map { result ->
        when (result) {
            is Result.Pending -> Loading
            is Result.Success -> Idle(videos = result.value)
            is Result.Failure -> Failure(result.exception.message ?: "Unknown error")
        }
    }

    fun reload() {
        data.load()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}
