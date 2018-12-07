package com.novoda.redditvideos

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.KStubbing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.novoda.reddit.data.Listing
import com.novoda.reddit.data.ListingService
import com.novoda.reddit.data.Preview
import com.novoda.reddit.data.Thing
import com.novoda.redditvideos.model.VideoDao
import com.novoda.testsupport.stub
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.novoda.redditvideos.model.VideoFeedState
import org.junit.Rule

class VideoFeedViewModelTest {

    @Rule @JvmField val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `should load data from the network`() = givenAViewModel(
        withListingService = listingServiceThat {
            val successCall = successCall(expectedVideoResults)
            on { videos() } doReturn successCall
        }
    ) { viewModel ->

        assertThat(viewModel.state.value).isEqualTo(VideoFeedState(
            data = mock(),
            loadState = VideoFeedState.LoadState.Idle
        ))
    }


}

private fun listingServiceThat(stubbing: KStubbing<ListingService>.(ListingService) -> Unit) =
    mock(stubbing = stubbing)

private fun <T> successCall(result: T) = mock<Call<T>> {
    on { execute() } doReturn Response.success(result)
}

private val expectedVideoResults = Listing(
    children = listOf(
        Thing.Post(
            title = "Some amazing video",
            name = "t3_seerg4",
            thumbnail = "https://myvideo.com/amazing",
            preview = Preview(emptyList())
        )
    )
)

private fun givenAViewModel(
    withVideoDao: VideoDao = stub(),
    withListingService: ListingService = stub(),
    f: (VideoFeedViewModel) -> Unit
) {
    val viewModel = VideoFeedViewModel(stub(), withVideoDao, withListingService)
    f(viewModel)
}


