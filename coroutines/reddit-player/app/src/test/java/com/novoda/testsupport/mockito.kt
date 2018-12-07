package com.novoda.testsupport

import com.nhaarman.mockitokotlin2.mock

inline fun <reified T : Any> stub() = mock<T>(stubOnly = true)

