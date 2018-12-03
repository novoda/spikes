package com.novoda.redditvideos.support.view

import android.view.View
import android.widget.ProgressBar
import kotlinx.coroutines.*

/**
 * Hides the [ProgressBar] and reveal it [after] a given time (500 milliseconds by default)
 */
fun ProgressBar.revealDelayed(after: Long = 500) = GlobalScope.launch {
    withContext(Dispatchers.Main) { visibility = View.GONE }
    delay(after)
    withContext(Dispatchers.Main) { visibility = View.VISIBLE }
}
