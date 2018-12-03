package com.novoda.redditvideos.support.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) =
    observe(lifecycleOwner, Observer { observer(it) })

fun <T, R> LiveData<T>.map(f: (T) -> R): LiveData<R> = Transformations.map(this, f)
