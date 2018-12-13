package com.novoda.testsupport

import com.nhaarman.mockitokotlin2.mock
import org.mockito.Mockito.RETURNS_DEEP_STUBS

inline fun <reified T : Any> stub() = mock<T>(stubOnly = true)

