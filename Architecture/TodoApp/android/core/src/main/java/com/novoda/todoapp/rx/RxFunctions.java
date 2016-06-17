package com.novoda.todoapp.rx;

import rx.Observable;
import rx.functions.Func1;

public final class RxFunctions {

    private RxFunctions() {
        throw new IllegalStateException("NonInstantiableClassException");
    }

    public static <T, V> Func1<T, Observable<V>> ifThenMap(final IfThenFlatMap<T, V> ifThenFlatMap) {
        return new Func1<T, Observable<V>>() {
            @Override
            public Observable<V> call(T value) {
                if (ifThenFlatMap.ifMatches(value)) {
                    return ifThenFlatMap.thenMap(value);
                } else {
                    return ifThenFlatMap.elseMap(value);
                }
            }
        };
    }

}
