package com.novoda.hermes

class Hermes {

    private val brokers = mutableListOf<Broker<*>>()

    fun track(event: Any) {
        brokers.forEach { it.track(event) }
    }

    fun <A> register(broker: Broker<A>): Hermes {
        brokers += broker
        return this
    }

}

class Broker<A> private constructor(
    private val type: Class<A>,
    private vararg val consumers: Consumer<A>
) {

    companion object {

        @JvmStatic @JvmName("broker") @SafeVarargs
        operator fun <A> invoke(type: Class<A>, vararg rest: Consumer<A>): Broker<A> {
            return Broker(type, *rest)
        }

        operator inline fun <reified A> invoke(vararg rest: Consumer<A>): Broker<A> {
            return invoke(A::class.java, *rest)
        }

    }

    @Suppress("UNCHECKED_CAST")
    internal fun track(event: Any) {
        if (type.isInstance(event)) {
            consumers.forEach { it.consume(event as A) }
        }
    }

}

interface Consumer<in A> {

    companion object {
        operator fun <A> invoke(f: (A) -> Unit) = object : Consumer<A> {
            override fun consume(event: A) = f(event)
        }
    }

    fun consume(event: A)

}
