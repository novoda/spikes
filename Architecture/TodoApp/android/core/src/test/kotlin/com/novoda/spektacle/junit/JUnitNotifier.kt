package com.novoda.spektacle.junit

import com.novoda.spektacle.SpektacleTree
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier

class JUnitNotifier<T>(val runNotifier: RunNotifier, val junitDescriptionCache: JUnitDescriptionCache<T>) : Notifier<T> {
    override fun start(key: SpektacleTree<T>) {
        runNotifier.fireTestStarted(junitDescriptionCache.get(key))
    }

    override fun succeed(key: SpektacleTree<T>) {
        runNotifier.fireTestFinished(junitDescriptionCache.get(key))
    }

    override fun fail(key: SpektacleTree<T>, error: Throwable) {
        val description = junitDescriptionCache.get(key)
        runNotifier.fireTestFailure(Failure(description, error))
        runNotifier.fireTestFinished(description)
    }

    override fun ignore(key: SpektacleTree<T>) {
        val description = junitDescriptionCache.get(key)

        runNotifier.fireTestIgnored(description)
        runNotifier.fireTestFinished(description)
    }
}
