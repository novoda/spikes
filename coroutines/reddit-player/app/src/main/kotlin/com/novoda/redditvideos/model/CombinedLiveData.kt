package com.novoda.redditvideos.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class CombinedLiveData<T>(
    private val initialValue: T,
    job: Job,
    val getFirst: () -> T,
    val getSecond: () -> T
) : LiveData<T>(), CoroutineScope {

    override val coroutineContext = GlobalScope.coroutineContext + job

    init {
        value = initialValue
    }

    fun reload() {
        value = initialValue
        load()
    }

    fun load() = launch(Main) {
        value = async(IO) { getFirst() }.await()
        value = async(IO) { getSecond() }.await()
    }

}
