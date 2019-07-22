package com.novoda.movies.gallery

import com.novoda.movies.core.NetworkingDependencyProvider
import com.novoda.movies.core.UI

class GalleryDependencyProvider(private val networkingDependencyProvider: NetworkingDependencyProvider = NetworkingDependencyProvider()) {

    private fun provideGalleryBackend(): GalleryBackend = KtorGalleryBackend(networkingDependencyProvider.provideAuthenticatedClient())

    private fun provideGalleryFetcher(): GalleryFetcher = GalleryFetcher(provideGalleryBackend())

    fun providerPresenter(): GalleryPresenter = GalleryPresenter(UI, provideGalleryFetcher())

}
