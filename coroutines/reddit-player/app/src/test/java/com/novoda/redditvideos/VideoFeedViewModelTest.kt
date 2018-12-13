package com.novoda.redditvideos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.novoda.reddit.data.ListingService
import com.novoda.redditvideos.model.VideoDao
import com.novoda.redditvideos.model.VideoFeedState
import com.novoda.redditvideos.model.VideoFeedState.LoadState.*
import com.novoda.testsupport.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class VideoFeedViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `should load data from the network`() = givenAViewModel(
        withListingServiceThat = mock {
            on { videos() } doAnswer { successCall(initialPageResults) }
        }
    ) {
        receive().apply {
            assertThat(loadState).isEqualTo(Loading)
            assertThat(data).isEmpty()
        }

        receive().apply {
            assertThat(loadState).isEqualTo(Idle)
            assertThat(data).containsExactlyElementsIn(expectedInitialListings).inOrder()
        }
    }

    @Test
    fun `should report error when network fails`() = givenAViewModel(
        withListingServiceThat = mock {
            on { videos() } doAnswer { throw TestException }
        }
    ) {
        receive().apply {
            assertThat(loadState).isEqualTo(Loading)
            assertThat(data).isEmpty()
        }

        receive().apply {
            assertThat(loadState).isEqualTo(Failure(TestException))
            assertThat(data).isEmpty()
        }
    }

    @Test
    fun `should load next network page`() = givenAViewModel(
        withListingServiceThat = mock {
            on { videos() } doAnswer { withDelay { successCall(initialPageResults) } }
            on { videos(after = "t3_a4hjzs") } doAnswer { withDelay { successCall(secondPageResults) } }
        }
    ) {

        receive().apply {
            assertThat(loadState).isEqualTo(Loading)
            assertThat(data).isEmpty()
        }

        receive().apply {
            assertThat(loadState).isEqualTo(Idle)
            assertThat(data).containsExactlyElementsIn(expectedInitialListings).inOrder()
        }.data.loadAround(24)

        receive().apply {
            assertThat(loadState).isEqualTo(Loading)
            assertThat(data).containsExactlyElementsIn(expectedInitialListings).inOrder()
        }

        receive().apply {
            assertThat(loadState).isEqualTo(Idle)
            assertThat(data).containsExactlyElementsIn(expectedInitialListings + expectedSecondListings).inOrder()
        }
    }


    @Test
    fun `should load data from database before the network`() = givenAViewModel(
        withVideoDao = mock {
            on { findVideos() } doReturn testDataSourceFactory(daoEntries)
        },
        withListingServiceThat = mock {
            on { videos() } doAnswer { withDelay { successCall(initialPageResults) } }
        }
    ) {
        receive().apply {
            assertThat(loadState).isEqualTo(Loading)
            assertThat(data).containsAllIn(expectedDaoVideos).inOrder()
        }

        receive().apply {
            assertThat(loadState).isEqualTo(Idle)
            assertThat(data).containsAllIn(expectedInitialListings).inOrder()
        }
    }

}

private object TestException : Exception()

private fun givenAViewModel(
    withVideoDao: VideoDao = mock {
        on { findVideos() } doReturn testDataSourceFactory(emptyList())
    },
    withListingServiceThat: ListingService = stub(),
    block: suspend Channel<VideoFeedState>.() -> Unit
) {
    val viewModel = VideoFeedViewModel(
        app = stub(),
        videoDao = withVideoDao,
        job = Job(),
        listingService = withListingServiceThat,
        mainDispatcher = Dispatchers.Default
    )
    val states = Channel<VideoFeedState>(Channel.UNLIMITED).apply {
        viewModel.state.observeForever {
            println("New state: $it")
            offer(it)
        }
    }
    runBlocking {
        states.block()
    }
}


