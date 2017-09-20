package novoda.com.sandbox.repositories;

import novoda.com.sandbox.dao.UserDao;
import novoda.com.sandbox.models.User;
import novoda.com.sandbox.services.UserCreationService;

public class UserRepository {
    private UserCreationService userCreationService;
    private UserDao userDao;

    public UserRepository() {
        userCreationService = new UserCreationService();
        userDao = new UserDao();
    }

    public User getValidUser() {
        if (userDao.validUserAvailable()) {
            return userDao.fetchValidUserFromDatabase();
        } else {
            return userCreationService.createNewUser();
        }
    }

    public User getValidAdminUser() {
        if (userDao.validAdminUserAvailable()) {
            return userDao.fetchValidAdminUserFromDatabase();
        } else {
            return userCreationService.createNewAdminUser();
        }
    }

    public User getUserWithTooShortUsername() {
        return userCreationService.createUserWithTooShortUsername();
    }

    public User getUserWithTooShortPassword() {
        return userCreationService.createUserWithTooShortPassword();
    }
}
