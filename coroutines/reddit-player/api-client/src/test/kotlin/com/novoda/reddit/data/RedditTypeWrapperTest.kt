package com.novoda.reddit.data

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

internal class RedditTypeWrapperTest {

    @Test
    fun `loads list of videos`() {
        val videos = testRedditApi.listingService.videos().execute().body()

        assertThat(videos).isNotNull()
    }

}

private val testRedditApi = RedditApi(createRetrofit(baseUrl = "https://reddit.com"))
