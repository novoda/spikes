package novoda.com.sandbox.services;

import com.github.javafaker.Faker;

import java.util.Random;

import novoda.com.sandbox.models.Credentials;

class CredentialService {
    private final static int MIN_USERNAME_LENGTH = 4;
    private final static int MIN_PASSWORD_LENGTH = 4;
    private final static int MAX_USERNAME_LENGTH = 10;
    private final static int MAX_PASSWORD_LENGTH = 10;

    private Faker faker;
    private Random random;

    CredentialService() {
        faker = new Faker();
        random = new Random();
    }

    Credentials getValidCredentials() {
        String username = createValidUsername();
        String password = createValidPassword();
        return new Credentials(username, password);
    }

    Credentials getCredentialsWithTooShortUsername() {
        String username = faker.lorem().characters(createTooShortUsernameLength());
        String password = createValidPassword();
        return new Credentials(username, password);
    }

    Credentials getCredentialsWithTooShortPassword() {
        String username = createValidUsername();
        String password = faker.internet().password(1, MIN_PASSWORD_LENGTH - 1);
        return new Credentials(username, password);
    }

    private String createValidUsername() {
        return faker.lorem().characters(createRandomUsernameLength());
    }

    private String createValidPassword() {
        return faker.internet().password(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH);
    }

    private int createRandomUsernameLength() {
        return random.nextInt(MAX_USERNAME_LENGTH) + MIN_USERNAME_LENGTH;
    }

    private int createTooShortUsernameLength() {
        return random.nextInt(MIN_USERNAME_LENGTH - 1) + 1;
    }


}
