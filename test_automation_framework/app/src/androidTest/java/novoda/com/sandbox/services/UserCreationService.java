package novoda.com.sandbox.services;

import novoda.com.sandbox.models.Credentials;
import novoda.com.sandbox.models.User;

public class UserCreationService {
    private CredentialService credentialService;

    public UserCreationService() {
        credentialService = new CredentialService();
    }

    public User createNewUser() {
        Credentials credentials = credentialService.getValidCredentials();
        return new User.UserBuilder(credentials).build();
    }

    public User createUserWithGivenUsername(String username) {
        Credentials credentials = credentialService.getCredentialsWithGivenUsername(username);
        return new User.UserBuilder(credentials).build();
    }

    public User createNewAdminUser() {
        Credentials credentials = credentialService.getValidCredentials();
        return new User.UserBuilder(credentials).withAdminStatus().build();
    }

    public User createUserWithTooShortUsername() {
        Credentials credentials = credentialService.getCredentialsWithTooShortPassword();
        return new User.UserBuilder(credentials).build();
    }

    public User createUserWithTooShortPassword() {
        Credentials credentials = credentialService.getCredentialsWithTooShortUsername();
        return new User.UserBuilder(credentials).build();
    }
}
