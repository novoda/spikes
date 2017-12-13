package com.novoda.hermes.demo

import com.novoda.hermes.Broker
import com.novoda.hermes.Consumer
import com.novoda.hermes.Hermes
import java.util.*
import kotlin.system.exitProcess

object Application {

    private val hermes = Hermes()
        .register(Broker(Consumer<String> { log("Event logged: " + it) }))
        .register(Broker(MeConsumer()))
        .register(Broker(NovodaConsumer()))
        .register(Broker(MurrayConsumer()))
        .register(Broker(HelloConsumer(), HelloMeetConsumer()))

    private fun log(message: String) {
        println(message)
    }

    @JvmStatic
    fun main(vararg args: String) {
        runApp()
    }

    tailrec private fun runApp() {
        readLine()?.let { input ->
            if (input == "exit") {
                log("Bye, bye!").also { exitProcess(0) }
            } else {
                hermes.track(input)
                hermes.track(Event.parse(input))
            }
        }
        runApp()
    }

    sealed class Event {

        companion object {
            fun parse(value: String): Event = when {
                value == "hermes" -> Hermes
                value == "novoda" -> Novoda
                value == "murray" -> Murray
                value.startsWith("my name is ") -> Hello(value.replace("my name is ", ""))
                else -> None
            }
        }

        object Hermes : Event()
        object Novoda : Event()
        object Murray : Event()
        data class Hello(val name: String) : Event()
        object None : Event()
    }

    private class MurrayConsumer : Consumer<Event.Murray> {

        private val random = Random()

        override fun consume(event: Event.Murray) {
            val width = random.nextInt(1000)
            val height = random.nextInt(1000)
            log("Have a murray: http://www.fillmurray.com/$width/$height")
        }
    }

    private class NovodaConsumer : Consumer<Event.Novoda> {

        override fun consume(event: Event.Novoda) {
            log("That's a cool place!")
        }

    }

    private class MeConsumer : Consumer<Event.Hermes> {

        override fun consume(event: Event.Hermes) {
            log("That's me!!!")
        }

    }

    private class HelloConsumer : Consumer<Event.Hello> {

        override fun consume(event: Event.Hello) {
            log("Well, hello there ${event.name}!")
        }

    }

    private class HelloMeetConsumer : Consumer<Event.Hello> {

        override fun consume(event: Event.Hello) {
            log("${event.name}, nice to meet you.")
        }

    }

}
