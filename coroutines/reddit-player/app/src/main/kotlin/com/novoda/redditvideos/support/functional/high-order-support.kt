package com.novoda.redditvideos.support.functional

/**
 * Attaches a continuation with the result of the initial function
 */
inline fun <T> (() -> T).then(crossinline f: (T) -> Unit): () -> T = {
    this().also(f)
}
