package sample

expect class Sample() {
    fun checkMe(): Int
}

expect object Platform {
    fun name(): String
}

fun hello(): String = "Hello from ${Platform.name()}"