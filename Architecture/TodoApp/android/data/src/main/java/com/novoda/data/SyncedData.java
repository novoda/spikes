package com.novoda.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SyncedData<T> {

    public static <T> SyncedData<T> from(T data, SyncState syncState, long lastSyncAction) {
        return new AutoValue_SyncedData<>(data, syncState, lastSyncAction);
    }

    SyncedData() {
        // AutoValue best practices https://github.com/google/auto/blob/master/value/userguide/practices.md
    }

    public abstract T data();

    public abstract SyncState syncState();

    public abstract long lastSyncAction();

}
