package com.novoda.spektacle

import com.novoda.spektacle.junit.Notifier

class SpektacleStepRunner<T>(val definedAs: (T?) -> T?) {

    fun run(tree: SpektacleTree<T>, init: T?, notifier: Notifier<T>, innerAction: (T?) -> Unit) {
        notifier.start(tree)
        try {
            innerAction(definedAs(init))
            notifier.succeed(tree)
        } catch(e: Throwable) {
            notifier.fail(tree, e)
        }
    }

}

