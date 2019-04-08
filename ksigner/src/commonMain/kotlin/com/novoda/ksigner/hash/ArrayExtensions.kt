package com.novoda.ksigner.hash

// IntArray

/**
 * Converts the given int array into a byte array via big-endian conversion
 * (1 int becomes 4 bytes).
 * @return The converted array.
 */
internal fun IntArray.toByteArray(): ByteArray {
    val array = ByteArray(this.size * 4)
    for (i in this.indices) {
        val bytes = this[i].toBytes()
        array[i * 4] = bytes[0]
        array[i * 4 + 1] = bytes[1]
        array[i * 4 + 2] = bytes[2]
        array[i * 4 + 3] = bytes[3]
    }
    return array
}

/**
 * Copies [length] elements present inside [this] starting from [srcPos] into [dest] starting from [destPos].
 * Thanks to manu0466
 */
internal fun IntArray.arrayCopy(srcPos: Int, dest: IntArray, destPos: Int, length: Int) {
    // Make a deep copy avoiding errors when this == dest
    val temp = this.copyOf()
    (0 until length).forEach { dest[destPos + it] = temp[srcPos + it] }
}


// ByteArray

/**
 * Converts the given byte array into an int array via big-endian conversion
 * (4 bytes become 1 int).
 * @return The converted array.
 */
internal fun ByteArray.toIntArray(): IntArray {
    if (this.size % INT_BYTES != 0) {
        throw IllegalArgumentException("Byte array length must be a multiple of $INT_BYTES")
    }

    val array = IntArray(this.size / INT_BYTES)
    for (i in array.indices) {
        val integer =
            arrayOf(this[i * INT_BYTES], this[i * INT_BYTES + 1], this[i * INT_BYTES + 2], this[i * INT_BYTES + 3])
        array[i] = integer.toInt()
    }
    return array
}

/**
 * Copies [length] elements present inside [this] starting from [srcPos] into [dest] starting from [destPos].
 * Thanks to manu0466
 */
internal fun ByteArray.arrayCopy(srcPos: Int, dest: ByteArray, destPos: Int, length: Int) {
    // Make a deep copy avoiding errors when this == dest
    val temp = this.copyOf()
    (0 until length).forEach { dest[destPos + it] = temp[srcPos + it] }
}

/**
 * Writes a long split into 8 bytes.
 * @param [offset] start index
 * @param [value] the value to insert
 * Thanks to manu0466
 */
internal fun ByteArray.putLong(offset: Int, value: Long) {
    for (i in 7 downTo 0) {
        val temp = ((value ushr (i * 8)) and 0xff).toInt()
        this[offset + 7 - i] = temp.toUByte()
    }
}


// Array<Byte>

/**
 * Converts 4 bytes into their integer representation following the big-endian conversion.
 */
internal fun Array<Byte>.toInt(): Int {
    return (this[0].toUInt() shl 24) + (this[1].toUInt() shl 16) + (this[2].toUInt() shl 8) + (this[3].toUInt() shl 0)
}

/**
 * Converts byte array to hex String
 */
@ExperimentalUnsignedTypes
fun ByteArray.toHexString() = asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }
