package com.novoda.movies.gallery

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

private const val BASE_URL: String = "https://api.themoviedb.org"
private const val KEY_API_SECRET: String = "api_key"
private const val API_SECRET: String = "your themoviedb api secret" //https://developers.themoviedb.org/3

class KtorGalleryBackend : GalleryBackend {

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer().apply {
                setMapper(ApiMoviePoster::class, ApiMoviePoster.serializer())
            }
        }
    }

    override suspend fun popularMoviesGallery(): ApiGallery {
        return client.get {
            url("$BASE_URL/3/movie/popular") {
                parameter(KEY_API_SECRET, API_SECRET)
            }
        }
    }
}
