package com.novoda.movies.core

import com.novoda.movies.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.parameter

private const val KEY_API_SECRET: String = "api_key"
private const val KEY_AUTH_INTERCEPTOR = "auth_interceptor"

class NetworkingDependencyProvider {

    fun provideAuthenticatedClient() = HttpClient {
        authenticatedRequestsEngine()
    }

    private fun HttpClientConfig<*>.authenticatedRequestsEngine() {
        engine {
            this@authenticatedRequestsEngine.install(KEY_AUTH_INTERCEPTOR) {
                this@authenticatedRequestsEngine.defaultRequest {
                    parameter(KEY_API_SECRET, BuildKonfig.API_KEY)
                }
            }
        }
    }
}
