package com.novoda.ksigner.hash

/**
 * Digest Class for SHA-256.
 * Original Java version at https://github.com/meyfa/java-sha256/blob/master/src/main/java/net/meyfa/sha256/Sha256.java
 *
 * @author Fabian Meyer (meyfa) - Original Java version
 * @author Riccardo Montagnin (RiccardoM) - Kotlin version
 *
 */
object Sha256 {
    private val K = intArrayOf(
        0x428a2f98,
        0x71374491,
        -0x4a3f0431,
        -0x164a245b,
        0x3956c25b,
        0x59f111f1,
        -0x6dc07d5c,
        -0x54e3a12b,
        -0x27f85568,
        0x12835b01,
        0x243185be,
        0x550c7dc3,
        0x72be5d74,
        -0x7f214e02,
        -0x6423f959,
        -0x3e640e8c,
        -0x1b64963f,
        -0x1041b87a,
        0x0fc19dc6,
        0x240ca1cc,
        0x2de92c6f,
        0x4a7484aa,
        0x5cb0a9dc,
        0x76f988da,
        -0x67c1aeae,
        -0x57ce3993,
        -0x4ffcd838,
        -0x40a68039,
        -0x391ff40d,
        -0x2a586eb9,
        0x06ca6351,
        0x14292967,
        0x27b70a85,
        0x2e1b2138,
        0x4d2c6dfc,
        0x53380d13,
        0x650a7354,
        0x766a0abb,
        -0x7e3d36d2,
        -0x6d8dd37b,
        -0x5d40175f,
        -0x57e599b5,
        -0x3db47490,
        -0x3893ae5d,
        -0x2e6d17e7,
        -0x2966f9dc,
        -0xbf1ca7b,
        0x106aa070,
        0x19a4c116,
        0x1e376c08,
        0x2748774c,
        0x34b0bcb5,
        0x391c0cb3,
        0x4ed8aa4a,
        0x5b9cca4f,
        0x682e6ff3,
        0x748f82ee,
        0x78a5636f,
        -0x7b3787ec,
        -0x7338fdf8,
        -0x6f410006,
        -0x5baf9315,
        -0x41065c09,
        -0x398e870e
    )

    private val H0 =
        intArrayOf(0x6a09e667, -0x4498517b, 0x3c6ef372, -0x5ab00ac6, 0x510e527f, -0x64fa9774, 0x1f83d9ab, 0x5be0cd19)

    // Working arrays
    private val W = IntArray(64)
    private val H = IntArray(8)
    private val TEMP = IntArray(8)

    /**
     * Hashes the given message with SHA-256 and returns the digest.
     *
     * @param message The bytes to digest.
     * @return The digest's bytes.
     */
    fun digest(message: ByteArray): ByteArray {
        // Let H = H0
        H0.arrayCopy(0, H, 0, H0.size)

        // Initialize all words
        val words = padMessage(message).toIntArray()

        // Enumerate all blocks (each containing 16 words)
        var i = 0
        val n = words.size / 16
        while (i < n) {

            // initialize W from the block's words
            words.arrayCopy(i * 16, W, 0, 16)
            for (t in 16 until W.size) {
                W[t] = (smallSig1(W[t - 2]) + W[t - 7] + smallSig0(W[t - 15]) + W[t - 16])
            }

            // Let TEMP = H
            H.arrayCopy(0, TEMP, 0, H.size)

            // Operate on TEMP
            for (t in W.indices) {
                val t1 = (TEMP[7] + bigSig1(TEMP[4]) + ch(TEMP[4], TEMP[5], TEMP[6]) + K[t] + W[t])
                val t2 = bigSig0(TEMP[0]) + maj(TEMP[0], TEMP[1], TEMP[2])
                TEMP.arrayCopy(0, TEMP, 1, TEMP.size - 1)
                TEMP[4] += t1
                TEMP[0] = t1 + t2
            }

            // Add values in TEMP to values in H
            for (t in H.indices) {
                H[t] += TEMP[t]
            }

            ++i
        }

        return H.toByteArray()
    }

    /**
     * Internal method, no need to call. Pads the given message to have a length
     * that is a multiple of 512 bits (64 bytes), including the addition of a
     * 1-bit, k 0-bits, and the message length as a 64-bit integer.
     *
     * @param message The message to padMessage.
     * @return A new array with the padded message bytes.
     */
    fun padMessage(message: ByteArray): ByteArray {
        val blockBits = 512
        val blockBytes = blockBits / 8

        // new message length: original + 1-bit and padding + 8-byte length
        var newMessageLength = message.size + 1 + 8
        val padBytes = blockBytes - newMessageLength % blockBytes
        newMessageLength += padBytes

        // copy message to extended array
        val paddedMessage = ByteArray(newMessageLength)
        message.arrayCopy(0, paddedMessage, 0, message.size)

        // write 1-bit
        paddedMessage[message.size] = 128.toByte()

        // skip padBytes many bytes (they are already 0)

        // write 8-byte integer describing the original message length
        val lenPos = message.size + 1 + padBytes

        paddedMessage.putLong(lenPos, message.size * 8.toLong())

        return paddedMessage
    }


    private fun ch(x: Int, y: Int, z: Int): Int {
        return x and y or (x.inv() and z)
    }

    private fun maj(x: Int, y: Int, z: Int): Int {
        return x and y or (x and z) or (y and z)
    }

    private fun bigSig0(x: Int): Int {
        return (x.rotateRight(2) xor x.rotateRight(13)
                xor x.rotateRight(22))
    }

    private fun bigSig1(x: Int): Int {
        return (x.rotateRight(6) xor x.rotateRight(11)
                xor x.rotateRight(25))
    }

    private fun smallSig0(x: Int): Int {
        return (x.rotateRight(7) xor x.rotateRight(18)
                xor x.ushr(3))
    }

    private fun smallSig1(x: Int): Int {
        return (x.rotateRight(17) xor x.rotateRight(19)
                xor x.ushr(10))
    }
}
