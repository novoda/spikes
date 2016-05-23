package com.novoda.bonfire.channel.data.model;

import com.novoda.bonfire.user.data.model.User;

public class UserSearchResult {

    private final User user;

    public UserSearchResult(User user) {
        this.user = user;
    }

    public UserSearchResult() {
        this.user = null;
    }

    public boolean isSuccess() {
        return user != null;
    }

    public User getUser() {
        return user;
    }
}
