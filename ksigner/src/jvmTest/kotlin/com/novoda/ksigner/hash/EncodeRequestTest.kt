package com.novoda.ksigner.hash

import okio.ByteString.Companion.encode
import org.junit.Test
import kotlin.test.assertEquals

class EncodeRequestTest {
    private val canonicalRequest = "PUT\n" +
            "/-/vaults/examplevault\n" +
            "\n" +
            "host:glacier.us-east-1.amazonaws.com\n" +
            "x-amz-date:20120525T002453Z\n" +
            "x-amz-glacier-version:2012-06-01\n" +
            "\n" +
            "host;x-amz-date;x-amz-glacier-version\n" +
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"


    @Test
    fun encodeRawCanonicalRequestToLowercaseHex() {
        val actual = canonicalRequest.encode().sha256().hex()
        val expected = "5f1da1a2d0feb614dd03d71e87928b8e449ac87614479332aced3a701f916743"

        assertEquals(expected, actual)
    }
}
