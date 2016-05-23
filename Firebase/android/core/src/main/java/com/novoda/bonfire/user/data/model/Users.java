package com.novoda.bonfire.user.data.model;

import java.util.List;

public class Users {

    private final List<User> users;

    public Users(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public int size() {
        return users.size() ;
    }

    public User getUserAt(int position) {
        return users.get(position);
    }
}
