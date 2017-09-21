package novoda.com.sandbox.dao;

import novoda.com.sandbox.models.Credentials;
import novoda.com.sandbox.models.User;

public class UserDao {
    public boolean validUserAvailable() {
        return false;
    }

    public boolean validAdminUserAvailable() {
        return false;
    }

    public boolean userWithUsernameAvailable() {
        return false;
    }

    public User fetchValidUserFromDatabase() {
        return new User.UserBuilder(new Credentials("testt", "test")).build();
    }

    public User fetchValidAdminUserFromDatabase() {
        return new User.UserBuilder(new Credentials("test", "test")).withAdminStatus().build();
    }

    public User fetchUserWithGivenUsername(String username) {
        return new User.UserBuilder(new Credentials(username, "asjfsf")).build();
    }
}
