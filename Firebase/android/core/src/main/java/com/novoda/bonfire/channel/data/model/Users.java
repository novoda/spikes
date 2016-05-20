package com.novoda.bonfire.channel.data.model;

import com.novoda.bonfire.login.data.model.User;

import java.util.List;

public class Users {

    private final List<User> users;

    public Users(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }
}
