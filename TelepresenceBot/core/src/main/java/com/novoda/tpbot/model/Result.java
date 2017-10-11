package com.novoda.tpbot.model;

public class Result {

    private final Optional<String> message;
    private final Optional<Exception> exception;

    public static Result from(String message) {
        return new Result(Optional.of(message), Optional.<Exception>absent());
    }

    public static Result from(Exception exception) {
        return new Result(Optional.<String>absent(), Optional.of(exception));
    }

    private Result(Optional<String> message, Optional<Exception> exception) {
        this.message = message;
        this.exception = exception;
    }

    public Optional<String> message() {
        return message;
    }

    public Optional<Exception> exception() {
        return exception;
    }

    public boolean isError() {
        return exception.isPresent();
    }

}
