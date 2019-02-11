package com.novoda.movies.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val UI: CoroutineDispatcher = Dispatchers.Main
