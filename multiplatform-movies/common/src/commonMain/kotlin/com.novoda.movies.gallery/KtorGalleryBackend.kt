package com.novoda.movies.gallery

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.url
import kotlinx.serialization.json.JSON

private const val BASE_URL: String = "https://api.themoviedb.org"

internal class KtorGalleryBackend(authenticatedClient: HttpClient) : GalleryBackend {

    private val galleryClient: HttpClient = authenticatedClient.config {
        install(JsonFeature) {
            serializer = KotlinxSerializer(JSON.nonstrict).apply {
                setMapper(ApiMoviePoster::class, ApiMoviePoster.serializer())
            }
        }
    }

    override suspend fun popularMoviesGallery(): ApiGallery {
        return galleryClient.get {
            url("$BASE_URL/3/movie/popular")
        }
    }
}
