package com.novoda.movies.core

import com.novoda.movies.gallery.ApiGallery
import com.novoda.movies.gallery.ApiMoviePoster
import com.novoda.movies.gallery.KtorGalleryBackend
import io.ktor.client.HttpClient
import io.ktor.client.call.ReceivePipelineException
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockHttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

private const val API_GALLERY_JSON = "{\"results\": [{\"id\":1, \"poster_path\":\"https://api.themoviedb.org/3/asset/1\"}]}"

//TODO: this test should be in the commonMain source set, but MockEngine can't be resolved as a dependency
class KtorGalleryBackendTest {

    @Test
    fun `should deserialize ApiGallery`() {
        val expectedGallery = ApiGallery(listOf(ApiMoviePoster(1, "https://api.themoviedb.org/3/asset/1")))

        val client = HttpClient(engineReturningResponse(API_GALLERY_JSON))

        runBlocking {
            val popularMoviesGallery = KtorGalleryBackend(client).popularMoviesGallery()
            assertEquals(popularMoviesGallery, expectedGallery)
        }
    }

    @Test(expected = ReceivePipelineException::class)
    fun `should error when json is malformed`() {
        val expectedGallery = ApiGallery(listOf(ApiMoviePoster(1, "https://api.themoviedb.org/3/asset/1")))

        val client = HttpClient(engineReturningResponse("some bad json"))

        runBlocking {
            val popularMoviesGallery = KtorGalleryBackend(client).popularMoviesGallery()
            assertEquals(popularMoviesGallery, expectedGallery)
        }
    }

    private fun engineReturningResponse(jsonString: String): MockEngine {
        return MockEngine {
            // this: HttpRequest, call: HttpClientCall
            when (url.fullPath) {
                "/3/movie/popular" -> {
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



