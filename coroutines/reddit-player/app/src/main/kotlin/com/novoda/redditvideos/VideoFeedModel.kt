package com.novoda.redditvideos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.novoda.reddit.data.ApiAware
import com.novoda.reddit.data.Listing
import com.novoda.reddit.data.Thing
import com.novoda.redditvideos.VideoFeedState.*
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import java.net.URLEncoder

class VideoFeedModel @JvmOverloads constructor(
    application: Application,
    override val retrofit: Retrofit = application.retrofit
) : AndroidViewModel(application), CoroutineScope, ApiAware {

    private val job = Job()

    override val coroutineContext = GlobalScope.coroutineContext + job

    private val _state = MutableLiveData<VideoFeedState>().apply {
        value = Loading
        reload()
    }

    val state: LiveData<VideoFeedState> = _state

    fun reload() {
        launch {
            val response = listing.videos().execute()
            val newState = when {
                response.isSuccessful -> Idle(videos = response.posts.map { it.toVideoEntry() })
                else -> Failure(response.message())
            }
            withContext(Dispatchers.Main) {
                _state.value = newState
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}

private val Response<Listing<Thing.Post>>.posts get() = body()?.children ?: emptyList()

private fun Thing.Post.toVideoEntry() = VideoEntry(
    title = title,
    thumbnail = Thumbnail(preview.images.getOrNull(0)?.source?.url?.decode() ?: "No image") // TODO() improve with error image
)

private fun String.decode() = replace("&amp;", "&")
