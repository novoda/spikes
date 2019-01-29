package com.novoda.movies.core

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.parameter

private const val KEY_API_SECRET: String = "api_key"
private const val API_SECRET: String = "your themoviedb api secret" //https://developers.themoviedb.org/3
private const val KEY_AUTH_INTERCEPTOR = "auth_interceptor"

class NetworkingDependencyProvider {

    fun provideAuthenticatedClient() = HttpClient {
        authenticatedRequestsEngine()
    }

    private fun HttpClientConfig<*>.authenticatedRequestsEngine() {
        engine {
            install(KEY_AUTH_INTERCEPTOR) {
                defaultRequest {
                    parameter(KEY_API_SECRET, API_SECRET)
                }
            }
        }
    }
}
