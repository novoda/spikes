package com.novoda.spektacle;

import com.novoda.spektacle.junit.JUnitSpektacleRunner
import com.novoda.spektacle.junit.Notifier
import org.junit.runner.RunWith

@RunWith(JUnitSpektacleRunner::class)
open class Spektacle<T>(val spekBody: Dsl<T>.() -> Unit) {
    val tree: SpektacleTree<T>

    init {
        val parentDescribeBody = DslParser<T>()
        parentDescribeBody.init(this.javaClass.simpleName, spekBody)
        tree = parentDescribeBody.children()[0]
    }

    fun run(notifier: Notifier<T>) {
        tree.run(null, notifier)
    }
}

interface Dsl<T> {

    fun definedAs(body: (T?) -> T?)

    fun init(description: String, body: Dsl<T>.() -> Unit)

    fun given(description: String, body: Dsl<T>.() -> Unit)

    fun on(description: String, body: Dsl<T>.() -> Unit)

    fun it(description: String, body:(T?) -> Unit)

}
