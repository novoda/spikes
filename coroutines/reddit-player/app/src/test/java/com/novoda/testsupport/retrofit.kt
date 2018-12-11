package com.novoda.testsupport

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import retrofit2.Call
import retrofit2.Response

fun <T> successCall(result: T) = mock<Call<T>> {
    on { execute() } doReturn Response.success(result)
}
