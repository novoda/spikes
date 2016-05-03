package com.novoda.todoapp.rx;

import rx.Observable;
import rx.functions.Func1;

public interface IfThenFlatMap<T, V> {

    boolean ifMatches(T value);

    Observable<V> thenMap(T value);

    Observable<V> elseMap(T value);

}
