package com.novoda.bonfire.channel.data.model;

public class ChannelWriteResult {

    private final Throwable failure;

    public ChannelWriteResult(Throwable failure) {
        this.failure = failure;
    }

    public ChannelWriteResult() {
        this.failure = null;
    }

    public boolean isFailure() {
        return failure != null;
    }

    public Throwable getFailure() {
        if (failure == null) {
            throw new IllegalStateException("Database write is successful please check with isSuccess first");
        }
        return failure;
    }
}
