package com.novoda.ksigner.hash

/**
 * Convert a Byte into an unsigned Int.
 * Source: https://stackoverflow.com/questions/38651192/how-to-correctly-handle-byte-values-greater-than-127-in-kotlin
 *
 * TODO: Remove this once Kotlin 1.3 is released as it brings support for UInt
 */
internal fun Byte.toUInt() = when {
    (toInt() < 0) -> 255 + toInt() + 1
    else -> toInt()
}
