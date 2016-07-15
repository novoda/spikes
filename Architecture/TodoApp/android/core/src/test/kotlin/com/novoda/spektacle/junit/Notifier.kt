package com.novoda.spektacle.junit

import com.novoda.spektacle.SpektacleTree

interface Notifier<T> {
    fun start(key: SpektacleTree<T>)
    fun succeed(key: SpektacleTree<T>)
    fun fail(key: SpektacleTree<T>, error: Throwable)
    fun ignore(key: SpektacleTree<T>)
}
