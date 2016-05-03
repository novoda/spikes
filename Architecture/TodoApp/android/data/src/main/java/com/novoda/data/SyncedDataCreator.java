package com.novoda.data;

public interface SyncedDataCreator<T> {

    SyncedData<T> startWith();

    SyncedData<T> onConfirmed(T value);

    SyncedData<T> onError();

}
