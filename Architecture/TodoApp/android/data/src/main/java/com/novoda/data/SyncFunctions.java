package com.novoda.data;

import rx.Observable;
import rx.functions.Func1;

public final class SyncFunctions {

    private SyncFunctions() {
        throw new IllegalStateException("NonInstantiableClassException");
    }

    public static <T> Observable.Transformer<T, SyncedData<T>> asSyncedAction(final SyncedDataCreator<T> syncedDataCreator) {
        return new Observable.Transformer<T, SyncedData<T>>() {
            @Override
            public Observable<SyncedData<T>> call(Observable<T> observable) {
                return observable
                        .map(new Func1<T, SyncedData<T>>() {
                            @Override
                            public SyncedData<T> call(T value) {
                                return syncedDataCreator.onConfirmed(value);
                            }
                        })
                        .onErrorReturn(new Func1<Throwable, SyncedData<T>>() {
                            @Override
                            public SyncedData<T> call(Throwable throwable) {
                                return syncedDataCreator.onError();
                            }
                        })
                        .startWith(syncedDataCreator.startWith());
            }
        };
    }

}
