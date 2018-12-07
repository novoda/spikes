package com.novoda.redditvideos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.novoda.reddit.data.ListingService
import com.novoda.redditvideos.model.DataContext
import com.novoda.redditvideos.model.VideoDao
import com.novoda.redditvideos.model.VideoFeedState
import com.novoda.redditvideos.model.videoFeed
import kotlinx.coroutines.*

internal class VideoFeedViewModel @JvmOverloads constructor(
    app: Application,
    videoDao: VideoDao = app.database.videoDao(),
    override val listingService: ListingService = app.apiClient.listingService,
    override val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    override val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AndroidViewModel(app), DataContext {

    private val job = Job()
    override val coroutineContext = GlobalScope.coroutineContext + job

    val state: LiveData<VideoFeedState> = videoFeed()

    fun reload() {
//        _state.reload()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}
