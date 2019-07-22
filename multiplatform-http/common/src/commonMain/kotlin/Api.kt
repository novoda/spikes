package com.novoda.playground.multiplatform.common

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineDispatcher

internal expect val ApplicationDispatcher: CoroutineDispatcher

class Api {
    private var url = Url("http://10.2.0.138:3000/recipe")

    private val client = HttpClient() {
        install(JsonFeature) {
            serializer = KotlinxSerializer().apply {
                setMapper(Recipe::class, Recipe.serializer())
            }
        }
    }

    suspend fun recipe(): Recipe {
        return client.get {
            url(this@Api.url.toString())
        }
    }
}

