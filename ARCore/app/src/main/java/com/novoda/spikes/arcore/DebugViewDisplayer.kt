package com.novoda.spikes.arcore

import android.os.Handler
import android.os.Looper
import android.widget.TextView


class DebugViewDisplayer(private val textView: TextView) {
    private val interval = 500L
    private val buffer = StringBuilder()
    private val buf = StringBuilder()
    private val handler = Handler(Looper.getMainLooper())
    private var scheduledMessage = false

    fun display() {
        if (!scheduledMessage) {
            scheduledMessage = true
            handler.postDelayed({
                textView.text = buf.toString()
                scheduledMessage = false
            }, interval)
        }

    }

    fun append(message: String) {
        buf.append(message)
        buf.append("\n")
    }
}