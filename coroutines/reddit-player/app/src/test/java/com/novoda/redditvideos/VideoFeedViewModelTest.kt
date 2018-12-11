package com.novoda.redditvideos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.novoda.reddit.data.ListingService
import com.novoda.redditvideos.model.VideoDao
import com.novoda.redditvideos.model.VideoFeedState
import com.novoda.redditvideos.model.VideoFeedState.LoadState.Idle
import com.novoda.redditvideos.model.VideoFeedState.LoadState.Loading
import com.novoda.testsupport.does
import com.novoda.testsupport.stub
import com.novoda.testsupport.successCall
import com.novoda.testsupport.waitFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class VideoFeedViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `should load data from the network`() = givenAViewModel(
        withListingServiceThat = mock {
            on { videos() } doAnswer { successCall(initialPageResults) }
        }
    ) { viewModel, stateChannel ->

        val states = listOf(stateChannel.receive(), stateChannel.receive())
        val actualVideos = viewModel.state.value?.data

        assertThat(states.map { it.loadState }).containsExactly(Loading, Idle)
        assertThat(actualVideos).containsAllIn(expectedInitialListings)
    }

    @Test
    fun `should report error when network fails`() = givenAViewModel(
        withListingServiceThat = mock {
            on { videos() } doAnswer { throw TestException }
        }
    ) { viewModel, states ->
        val (firstState, secondState) = listOf(states.receive(), states.receive())
        val actualVideos = viewModel.state.value?.data

        assertThat(firstState.loadState).isEqualTo(Loading)
        assertThat((secondState.loadState as VideoFeedState.LoadState.Failure).exception).isEqualTo(TestException)
        assertThat(actualVideos).isEmpty()
    }

    @Test
    fun `should load next network page`() = givenAViewModel(
        withListingServiceThat = mock {
            on { videos() } doAnswer { successCall(initialPageResults) }
            on { videos(after = "t3_a4hjzs") } doAnswer { successCall(secondPageResults) }
        }
    ) { viewModel, stateChannel ->
        val states = listOf(stateChannel.receive(), stateChannel.receive())
        val actualVideos = viewModel.state.value?.data!!
        actualVideos.loadAround(24)
        val newPageStates = listOf(stateChannel.receive(), stateChannel.receive())

        waitFor { actualVideos.size >= 50 }

        assertThat(states.map { it.loadState }).containsExactly(Loading, Idle)
        assertThat(newPageStates.map { it.loadState }).containsExactly(Loading, Idle)
        assertThat(actualVideos).does {
            hasSize(50)
            containsNoDuplicates()
            containsExactlyElementsIn(expectedInitialListings + expectedSecondListings).inOrder()
        }
    }

}

private object TestException : Exception()

private fun givenAViewModel(
    withVideoDao: VideoDao = stub(),
    withListingServiceThat: ListingService = stub(),
    block: suspend (VideoFeedViewModel, Channel<VideoFeedState>) -> Unit
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
            offer(it)
        }
    }
    runBlocking {
        block(viewModel, states)
    }
}


