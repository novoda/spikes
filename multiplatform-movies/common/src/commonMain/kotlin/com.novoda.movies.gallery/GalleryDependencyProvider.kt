package com.novoda.movies.gallery

import com.novoda.movies.core.NetworkingDependencyProvider

class GalleryDependencyProvider(private val networkingDependencyProvider: NetworkingDependencyProvider) {

    private fun provideGalleryBackend(): GalleryBackend = KtorGalleryBackend(networkingDependencyProvider.provideAuthenticatedClient())

    fun provideGalleryFetcher(): GalleryFetcher = GalleryFetcher(provideGalleryBackend())

}
