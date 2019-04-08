package com.novoda.ksigner.hash

const val INT_BYTES = 4


/**
 * Returns the value obtained by rotating the two's complement binary representation of the specified [Int] value
 * right by the specified number of bits.
 * (Bits shifted out of the right hand, or low-order, side reenter on the left, or high-order.)
 */
internal fun Int.rotateRight(distance: Int): Int {
    return this.ushr(distance) or (this shl -distance)
}


/**
 * Converts an [Int] to an array of [Byte] using the big-endian conversion.
 * (The [Int] will be converted into 4 bytes)
 */
internal fun Int.toBytes(): Array<Byte> {
    val result = ByteArray(INT_BYTES)
    result[0] = (this shr 24).toByte()
    result[1] = (this shr 16).toByte()
    result[2] = (this shr 8).toByte()
    result[3] = this.toByte()
    return result.toTypedArray()
}

/**
 * Converts an [Int] to an unsigned [Byte].
 * Thanks to manu0466
 *
 * TODO: Remove this once Kotlin 1.3 is released as it brings support for UByte
 */
internal fun Int.toUByte(): Byte = when {
    this < 128 -> toByte()
    else -> (-256 + this).toByte()
}
