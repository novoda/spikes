package com.odai.architecturedemo.event

data class Event<T> (val status: Status, val data: T?, val error: Throwable?)
