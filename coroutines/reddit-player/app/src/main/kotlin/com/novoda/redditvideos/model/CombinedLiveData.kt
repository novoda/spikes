package com.novoda.redditvideos.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class CombinedLiveData<T>(
    initialValue: T,
    job: Job,
    val getLocal: () -> T,
    val getRemote: () -> T
) : LiveData<T>(), CoroutineScope {

    override val coroutineContext = GlobalScope.coroutineContext + job

    init {
        value = initialValue
    }

    fun reload() = launch(Main) {
        value = async(IO) { getRemote() }.await()
    }

    fun load() = launch(Main) {
        value = async(IO) { getLocal() }.await()
        value = async(IO) { getRemote() }.await()
    }

}
