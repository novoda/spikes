package com.novoda.spektacle;

import com.novoda.spektacle.junit.Notifier

open class SpektacleTree<T>(val description: String,
                         val type: ActionType,
                         val runner: SpektacleStepRunner<T>,
                         val children: List<SpektacleTree<T>>) {

    fun run(init: T?, notifier: Notifier<T>) {
        runner.run(this, init, notifier) {
            val data = it;
            children.forEach {
                it.run(data, notifier)
            }
        }
    }

}

enum class ActionType {
    IT, GIVEN, ON, INIT
}


