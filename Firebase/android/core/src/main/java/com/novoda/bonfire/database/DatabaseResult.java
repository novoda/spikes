package com.novoda.bonfire.database;

public class DatabaseResult<T> {

    private final Throwable failure;
    private final T data;

    public DatabaseResult(Throwable failure) {
        this.failure = failure;
        data = null;
    }

    public DatabaseResult(T data) {
        this.failure = null;
        this.data = data;
    }

    public boolean isSuccess() {
        return data != null;
    }

    public Throwable getFailure() {
        if (failure == null) {
            throw new IllegalStateException("Database write is successful please check with isSuccess first");
        }
        return failure;
    }

    public T getData() {
        if (data == null) {
            throw new IllegalStateException("Database write is not successful please check with isSuccess first");
        }
        return data;
    }
}
