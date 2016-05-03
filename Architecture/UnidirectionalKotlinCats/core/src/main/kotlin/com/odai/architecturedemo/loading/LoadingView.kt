package com.odai.architecturedemo.loading

interface LoadingView {

    fun attach(retryListener: RetryClickedListener)

    fun showLoadingIndicator()

    fun showLoadingScreen()

    fun showData()

    fun showEmptyScreen()

    fun showErrorIndicator()

    fun showErrorScreen()


}
