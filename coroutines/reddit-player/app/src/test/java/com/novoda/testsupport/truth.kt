package com.novoda.testsupport

import com.google.common.truth.Subject

fun <S : Subject<S, T>, T> S.does(f: S.() -> Unit) = f()
