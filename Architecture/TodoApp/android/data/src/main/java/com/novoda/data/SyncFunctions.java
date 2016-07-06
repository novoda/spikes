package com.novoda.data;

import rx.Observable;
import rx.functions.Func1;

public final class SyncFunctions {

    private SyncFunctions() {
        throw new IllegalStateException("NonInstantiableClassException");
    }

    public static <T, V> Observable.Transformer<T, V> asOrchestratedAction(final DataOrchestrator<T, V> dataOrchestrator) {
        return new Observable.Transformer<T, V>() {
            @Override
            public Observable<V> call(Observable<T> observable) {
                return observable
                        .map(new Func1<T, V>() {
                            @Override
                            public V call(T value) {
                                return dataOrchestrator.onConfirmed(value);
                            }
                        })
                        .defaultIfEmpty(dataOrchestrator.onConfirmedWithoutData())
                        .onErrorReturn(new Func1<Throwable, V>() {
                            @Override
                            public V call(Throwable throwable) {
                                return dataOrchestrator.onError();
                            }
                        })
                        .startWith(dataOrchestrator.startWith());
            }
        };
    }
}
