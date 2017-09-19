package novoda.com.sandbox.services;

import novoda.com.sandbox.models.Credentials;
import novoda.com.sandbox.models.User;

public class UserService {
    private CredentialService credentialService;

    public UserService() {
        credentialService = new CredentialService();
    }

    public User getValidUser() {
        Credentials credentials = credentialService.getValidCredentials();
        return new User.UserBuilder(credentials).build();
    }

    public User createAdminUser() {
        Credentials credentials = credentialService.getValidCredentials();
        return new User.UserBuilder(credentials).withAdminStatus().build();
    }

    public User getUserWithTooShortPassword() {
        Credentials credentials = credentialService.getCredentialsWithTooShortPassword();
        return new User.UserBuilder(credentials).build();
    }

    public User getUserWithTooShortUsername() {
        Credentials credentials = credentialService.getCredentialsWithTooShortUsername();
        return new User.UserBuilder(credentials).build();
    }
}
