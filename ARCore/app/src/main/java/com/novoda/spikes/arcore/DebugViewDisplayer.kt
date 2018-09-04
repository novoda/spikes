package com.novoda.spikes.arcore

import android.os.Handler
import android.os.Looper
import android.widget.TextView


class DebugViewDisplayer(private val textView: TextView) {
    private val interval = 500L
    private val buffer = StringBuilder()
    private val handler = Handler(Looper.getMainLooper())
    private var scheduledMessage = false

    fun display() {
        if (!scheduledMessage) {
            scheduledMessage = true
            handler.postDelayed({
                textView.text = buffer.toString()
                scheduledMessage = false
            }, interval)
        }

    }

    fun append(message: String) {
        buffer.append(message)
        buffer.append("\n")
    }
}