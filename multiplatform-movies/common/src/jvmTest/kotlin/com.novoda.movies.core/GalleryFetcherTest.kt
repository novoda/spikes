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
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.io.charsets.Charsets
import kotlinx.io.core.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals

private const val API_GALLERY_JSON = "{\"results\": [{\"id\":1, \"poster_path\":\"https://api.themoviedb.org/3/asset/1\"}]}"
private const val GALLERY_PATH = "/3/movie/popular"

class GalleryFetcherTest {

    @Test
    fun `should fetch Gallery`() {
        // given
        val expectedGallery = Gallery(listOf(MoviePoster(1, Url("https://api.themoviedb.org/3/asset/1"))))
        val client = HttpClient(engineReturningResponse(API_GALLERY_JSON))
        val galleryFetcher = galleryFetcher(client)

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
        val client = HttpClient(engineReturningResponse("some bad formatted json"))
        val galleryFetcher = galleryFetcher(client)

        runBlocking {
            // when
            galleryFetcher.fetchGallery()
        }
    }

    private fun galleryFetcher(client: HttpClient): GalleryFetcher {
        val galleryBackend = KtorGalleryBackend(client)
        return GalleryFetcher(galleryBackend)
    }

    private fun engineReturningResponse(jsonString: String): MockEngine {
        return MockEngine {
            // this: HttpRequest, call: HttpClientCall
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



