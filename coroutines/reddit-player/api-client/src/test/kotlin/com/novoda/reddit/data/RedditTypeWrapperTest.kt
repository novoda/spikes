package com.novoda.reddit.data

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

internal class RedditTypeWrapperTest {

    @Test
    fun `loads list of videos`() {
        val videos = TestApiAware.listing.videos().execute().body()

        assertThat(videos).isNotNull()
    }

}

object TestApiAware : ApiAware {
    override val retrofit = createRetrofit(baseUrl = "https://reddit.com")
}
