package com.novoda.firechat.login.data.model;

public class Authentication {

    private final User user;
    private final Throwable failure;

    public Authentication(User user) {
        this.user = user;
        this.failure = null;
    }

    public Authentication(Throwable failure) {
        this.user = null;
        this.failure = failure;
    }

    public boolean isSuccess() {
        return user != null;
    }

    public User getUser() {
        if (user == null) {
            throw new IllegalStateException("Authentication is failed please check with isSuccess first");
        }
        return user;
    }

    public Throwable getFailure() {
        if (failure == null) {
            throw new IllegalStateException("Authentication is successful please check with isSuccess first");
        }
        return failure;
    }

}
