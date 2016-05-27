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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Users users1 = (Users) o;

        return users.equals(users1.users);

    }

    @Override
    public int hashCode() {
        return users.hashCode();
    }

    @Override
    public String toString() {
        return "Users{" +
                "users=" + users +
                '}';
    }
}
