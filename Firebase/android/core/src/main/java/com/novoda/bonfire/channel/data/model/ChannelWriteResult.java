package com.novoda.bonfire.channel.data.model;

public class ChannelWriteResult<T> {

    private final Throwable failure;
    private final T data;

    public ChannelWriteResult(Throwable failure) {
        this.failure = failure;
        data = null;
    }

    public ChannelWriteResult(T data) {
        this.failure = null;
        this.data = data;
    }

    public boolean isFailure() {
        return failure != null;
    }

    public boolean isSuccess() {
        return data != null;
    }

    public Throwable getFailure() {
        if (failure == null) {
            throw new IllegalStateException("Database write is successful please check with isFailure first");
        }
        return failure;
    }

    public T getData() {
        if (data == null) {
            throw new IllegalStateException("Database write is not successful please check with isFailure first");
        }
        return data;
    }
}
