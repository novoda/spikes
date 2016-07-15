package com.novoda.spektacle;

import java.util.*

class DslParser<T>() : Dsl<T> {

    val children = LinkedList<SpektacleTree<T>>()

    var definedAs: ((T?) -> T?)? = null;

    fun children(): List<SpektacleTree<T>> {
        if (children.isEmpty()) {
            throw RuntimeException(this.javaClass.canonicalName + ": no tests found")
        }
        return children
    }

    override fun definedAs(body: (T?) -> T?) {
        definedAs = body
    }

    override fun init(description: String, body: Dsl<T>.() -> Unit) {
        val inner = DslParser<T>()
        inner.body()
        children.add(SpektacleTree<T>(
                description,
                ActionType.INIT,
                SpektacleStepRunner<T>(inner.definedAs ?: { it }),
                inner.children))
    }

    override fun given(description: String, body: Dsl<T>.() -> Unit) {
        val inner = DslParser<T>()
        inner.body()
        children.add(SpektacleTree<T>(
                "given " + description,
                ActionType.GIVEN,
                SpektacleStepRunner<T>(inner.definedAs ?: { it }),
                inner.children))
    }

    override fun on(description: String, body: Dsl<T>.() -> Unit) {
        val inner = DslParser<T>()
        inner.body()
        children.add(SpektacleTree<T>(
                "on " + description,
                ActionType.ON,
                SpektacleStepRunner<T>(inner.definedAs ?: { it }),
                inner.children))
    }

    override fun it(description: String, body: (T?) -> Unit) {
        children.add(SpektacleTree<T>(
                "it " + description,
                ActionType.IT,
                SpektacleStepRunner<T>({
                    body.invoke(it)
                    return@SpektacleStepRunner it
                }),
                listOf()
        ))
    }


}
