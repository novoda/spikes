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

    @Override
    public String toString() {
        return "DatabaseResult{" +
                "failure=" + failure +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DatabaseResult<?> that = (DatabaseResult<?>) o;

        return failure != null ? failure.equals(that.failure) : that.failure == null
                && data != null ? data.equals(that.data) : that.data == null;
    }

    @Override
    public int hashCode() {
        int result = failure != null ? failure.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
