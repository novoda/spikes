package com.novoda.redditvideos.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import androidx.paging.PagedList
import com.novoda.reddit.data.Listing
import com.novoda.reddit.data.ListingService
import com.novoda.reddit.data.Thing
import com.novoda.redditvideos.model.VideoFeedState.LoadState.Loading
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

interface DataContext : CoroutineScope {

    val mainDispatcher: CoroutineDispatcher
    val ioDispatcher: CoroutineDispatcher
    val listingService: ListingService
    val videoDao: VideoDao
    val job: Job
    override val coroutineContext: CoroutineContext get() = ioDispatcher + job

    fun <K, V> DataSource<K, V>.toPagedList() =
        PagedList.Builder(this@toPagedList, 25)
            .setFetchExecutor { action -> launch(ioDispatcher) { action.run() } }
            .setNotifyExecutor { action -> launch(mainDispatcher) { action.run() } }
            .build()

}

fun DataContext.videoFeed(): LiveData<VideoFeedState> = MutableLiveData<VideoFeedState>().apply {
    launch {
        val localDataSource = videoDao.findVideos().create()
        val localVideos = localDataSource.map { it.toVideo() }.toPagedList()
        withContext(mainDispatcher) {
            value = VideoFeedState(Loading, localVideos)
        }
        val remoteDataSource = NetworkDataSource(listingService, CoroutineScope(ioDispatcher))
        val remoteVideos = remoteDataSource.map { it.toVideo() }.toPagedList()
        while (isActive) {
            val loadState = remoteDataSource.loadState.receive()
            if (loadState != value?.loadState) withContext(mainDispatcher) {
                value = VideoFeedState(loadState, remoteVideos)
            }
        }
    }
}

data class VideoFeedState(
    val loadState: LoadState,
    val data: PagedList<Video>
) {
    sealed class LoadState {
        object Loading : LoadState()
        object Idle : LoadState()
        data class Failure(val exception: Exception) : LoadState()
    }
}

private class NetworkDataSource(
    private val listingService: ListingService,
    private val coroutineScope: CoroutineScope
) : ItemKeyedDataSource<String, Thing.Post>() {

    val loadState = Channel<VideoFeedState.LoadState>()

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<Thing.Post>
    ) = load(
        createCall = { listingService.videos() },
        onResult = { callback.onResult(it) }
    )

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<Thing.Post>
    ) = load(
        createCall = { listingService.videos(after = params.key) },
        onResult = callback::onResult
    )

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<Thing.Post>
    ) = Unit // Not supported

    override fun getKey(item: Thing.Post) = item.name

    private fun load(
        createCall: () -> Call<Listing<Thing.Post>>,
        onResult: (List<Thing.Post>) -> Unit
    ) {
        coroutineScope.launch {
            try {
                loadState.send(Loading)
                val response = createCall().execute()
                val newState = when {
                    response.isSuccessful -> {
                        onResult(response.posts)
                        VideoFeedState.LoadState.Idle
                    }
                    else -> VideoFeedState.LoadState.Failure(NetworkError(response.code(), response.message()))
                }
                loadState.send(newState)
            } catch (e: Exception) {
                loadState.send(VideoFeedState.LoadState.Failure(e))
            }
        }
    }

}

data class NetworkError(val code: Int, val errorMessage: String) : RuntimeException(errorMessage)

private val Response<Listing<Thing.Post>>.posts get() = body()?.children ?: emptyList()

private fun Thing.Post.toVideo() = Video(
    title = title,
    previewUrl = PreviewUrl(
        preview.images.getOrNull(0)?.source?.url?.decode() ?: "No image"
    ),
    id = name
)

private fun VideoEntity.toVideo() = Video(
    title = title,
    previewUrl = PreviewUrl(previewUrl),
    id = id
)

private fun String.decode() = replace("&amp;", "&")

private val TAG = "VideoFeedRepository"

