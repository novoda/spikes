package com.novoda.hermes

class Hermes {

    private val brokers = mutableListOf<Broker<*>>()

    fun track(event: Any) {
        brokers.forEach { it.track(event) }
    }

    @Suppress("UNCHECKED_CAST")
    @SafeVarargs
    fun <A> register(type: Class<A>, vararg consumers: Consumer<A>): Hermes {
        val candidateBroker = brokers.find { it.type == type } as? Broker<A>?
        candidateBroker.let { broker ->
            when(broker) {
                null -> brokers += Broker(type, mutableListOf(*consumers))
                else -> broker.consumers += consumers
            }
        }
        return this
    }

    @SafeVarargs
    fun <A> register(type: Class<A>, vararg consumers: (A) -> Unit): Hermes =
        register(type, *consumers.map { Consumer(it) }.toTypedArray())

    inline fun <reified A> register(vararg consumers: Consumer<A>): Hermes =
        register(A::class.java, *consumers)

    inline fun <reified A> register(vararg consumers: (A) -> Unit): Hermes =
        register(A::class.java, *consumers)

    inline fun <reified A> register(consumer: Consumer<A>): Hermes =
        register(A::class.java, consumer)

    inline fun <reified A> register(noinline consumer: (A) -> Unit): Hermes =
        register(A::class.java, consumer)

}

private data class Broker<A>(
    val type: Class<A>,
    val consumers: MutableList<Consumer<A>>
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

@FunctionalInterface
interface Consumer<in A> {

    companion object {
        operator fun <A> invoke(f: (A) -> Unit) = object : Consumer<A> {
            override fun consume(event: A) = f(event)
        }
    }

    fun consume(event: A)

}
