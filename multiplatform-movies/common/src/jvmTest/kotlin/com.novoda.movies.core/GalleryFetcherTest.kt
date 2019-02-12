package com.novoda.movies.core

import com.novoda.movies.gallery.Gallery
import com.novoda.movies.gallery.GalleryFetcher
import com.novoda.movies.gallery.KtorGalleryBackend
import com.novoda.movies.gallery.MoviePoster
import io.ktor.client.HttpClient
import io.ktor.client.call.ReceivePipelineException
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockHttpResponse
import io.ktor.http.*
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.io.charsets.Charsets
import kotlinx.io.core.toByteArray
import org.junit.Test

private const val API_GALLERY_JSON = "{\"results\": [{\"id\":1, \"poster_path\":\"https://api.themoviedb.org/3/asset/1\"}]}"
private const val GALLERY_PATH = "/3/movie/popular"

class GalleryFetcherTest {

    @Test
    fun `should fetch Gallery`() {
        // given
        val expectedGallery = Gallery(listOf(MoviePoster(1, Url("https://api.themoviedb.org/3/asset/1"))))
        val galleryFetcher = galleryFetcherUsing(API_GALLERY_JSON)

        runBlocking {
            // when
            val popularMoviesGallery = galleryFetcher.fetchGallery()
            // then
            assertEquals(popularMoviesGallery, expectedGallery)
        }
    }

    @Test(expected = ReceivePipelineException::class)
    fun `should error when json is malformed`() {
        // given
        val galleryFetcher = galleryFetcherUsing("some bad formatted json")

        runBlocking {
            // when
            galleryFetcher.fetchGallery()
        }
    }

    private fun galleryFetcherUsing(fixture: String): GalleryFetcher {
        val client = HttpClient(usingFixedResponse(fixture))
        val galleryBackend = KtorGalleryBackend(client)
        return GalleryFetcher(galleryBackend)
    }

    private fun usingFixedResponse(jsonString: String): MockEngine {
        return MockEngine {
            when (url.fullPath) {
                GALLERY_PATH -> {
                    MockHttpResponse(
                            call,
                            HttpStatusCode.OK,
                            ByteReadChannel(jsonString.toByteArray(Charsets.UTF_8)),
                            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                    )
                }
                else -> {
                    error("Unhandled ${url.fullPath}")
                }
            }
        }
    }
}



