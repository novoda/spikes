package com.novoda.ksigner.hash

import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import kotlin.test.Test

class ArrayExtensionsTests {

    // INT ARRAY CONSTRUCTION

    @Test
    fun testToIntArrayEmpty() {
        val input = byteArrayOf()
        val expected = intArrayOf()
        Assert.assertArrayEquals(expected, input.toIntArray())
    }

    @Test
    fun testToIntSingle() {
        val input = byteArrayOf(0, 0, 0, 0)
        val expected = intArrayOf(0)
        Assert.assertArrayEquals(expected, input.toIntArray())

        val input2 = byteArrayOf(0, 0, 0, 1)
        val expected2 = intArrayOf(1)
        Assert.assertArrayEquals(expected2, input2.toIntArray())
    }

    @Test
    fun testToIntArrayMultiple() {
        val input = byteArrayOf(0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3)
        val expected = intArrayOf(1, 2, 3)
        Assert.assertArrayEquals(expected, input.toIntArray())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testToIntArrayThrowsForIllegalLength() {
        byteArrayOf(0, 0, 0, 1, 0).toIntArray()
    }

    // BYTE ARRAY CONSTRUCTION

    @Test
    fun testToByteArrayEmpty() {
        val input = intArrayOf()
        val expected = byteArrayOf()

        Assert.assertArrayEquals(expected, input.toByteArray())
    }

    @Test
    fun testToByteArrayMultiple() {
        val input = intArrayOf(1, 2, 3)
        val expected = byteArrayOf(0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3)

        Assert.assertArrayEquals(expected, input.toByteArray())
    }

    // ARRAY COPY

    @Test
    fun testIntArrayCopy() {
        val input = intArrayOf(1, 2, 3, 4, 5)
        val destination = IntArray(2)
        input.arrayCopy(1, destination, 0, 2)
        assertArrayEquals(intArrayOf(2, 3), destination)
    }

    // PUT LONG

    @Test
    fun testByteArrayPutLong() {
        val value = 0xaa_bb_cc_dd_ee_ff_99
        val byteArray = ByteArray(8)
        byteArray.putLong(0, value)
        assertArrayEquals(byteArrayOf(0, -86, -69, -52, -35, -18, -1, -103), byteArray)

        val value2 = -1L
        byteArray.putLong(0, value2)
        assertArrayEquals(byteArrayOf(-1, -1, -1, -1, -1, -1, -1, -1), byteArray)
    }
}
