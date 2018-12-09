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
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

interface DataContext : CoroutineScope {

    val mainDispatcher: CoroutineDispatcher
    val ioDispatcher: CoroutineDispatcher
    val listingService: ListingService
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
        val remoteDataSource = NetworkDataSource(listingService, CoroutineScope(ioDispatcher))
        val remoteVideos = remoteDataSource.map { it.toVideo() }.toPagedList()
        while (isActive) {
            val loadState = remoteDataSource.loadState.receive()
            withContext(mainDispatcher) {
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

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<Thing.Post>) =
        listingService.videos().load { callback.onResult(it) }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Thing.Post>) =
        listingService.videos(after = params.key).load(callback::onResult)

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Thing.Post>) =
        listingService.videos(before = params.key).load(callback::onResult)

    override fun getKey(item: Thing.Post) = item.name

    private fun Call<Listing<Thing.Post>>.load(onResult: (List<Thing.Post>) -> Unit) {
        coroutineScope.launch {
            loadState.send(Loading)
            val response = execute()
            val newState = when {
                response.isSuccessful -> {
                    onResult(response.posts)
                    VideoFeedState.LoadState.Idle
                }
                else -> VideoFeedState.LoadState.Failure(NetworkError(response.code(), response.message()))
            }
            loadState.send(newState)
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

private fun String.decode() = replace("&amp;", "&")
