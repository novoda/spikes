package com.novoda.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SyncedData<T> {

    public static <T> SyncedData<T> from(T data, SyncState syncState, long lastSyncAction) {
        return SyncedData.<T>builder()
                .data(data)
                .syncState(syncState)
                .lastSyncAction(lastSyncAction)
                .build();
    }

    public static <T> Builder<T> builder() {
        return new AutoValue_SyncedData.Builder<T>();
    }

    SyncedData() {
        // AutoValue best practices https://github.com/google/auto/blob/master/value/userguide/practices.md
    }

    public abstract T data();

    public abstract SyncState syncState();

    public abstract long lastSyncAction();

    public abstract Builder<T> toBuilder();

    @AutoValue.Builder
    public abstract static class Builder<T> {

        public abstract Builder<T> data(T data);

        public abstract Builder<T> syncState(SyncState syncState);

        public abstract Builder<T> lastSyncAction(long lastSyncAction);

        public abstract SyncedData<T> build();

    }

}
