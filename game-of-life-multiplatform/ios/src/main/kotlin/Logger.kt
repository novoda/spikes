package com.novoda.gol

actual object Logger {

    actual fun log(o: Any?) {
        print(o)
    }

}
