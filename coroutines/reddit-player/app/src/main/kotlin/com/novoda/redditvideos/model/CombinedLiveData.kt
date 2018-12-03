package com.novoda.redditvideos.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*

class CombinedLiveData<T>(
    private val initialValue: T,
    job: Job,
    private vararg val sources: () -> T
) : LiveData<T>(), CoroutineScope {

    override val coroutineContext = GlobalScope.coroutineContext + job

    init {
        value = initialValue
    }

    fun load(reset: Boolean = false) {
        if (reset) {
            value = initialValue
        }
        sources.forEach { fetch ->
            launch {
                val result = fetch()
                withContext(Dispatchers.Main) {
                    value = result
                }
            }
        }
    }

}
