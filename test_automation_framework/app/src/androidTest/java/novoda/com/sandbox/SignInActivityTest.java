package novoda.com.sandbox;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import novoda.com.sandbox.models.User;
import novoda.com.sandbox.repositories.UserRepository;
import novoda.com.sandbox.userFlows.LoginFlow;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {
    private final static String PASSWORD_USERNAME_TOO_SHORT = "Oops something went wrong, is your username " +
            "and password more than 4 characters?";

    private UserRepository userRepository;
    private LoginFlow loginFlow;

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void setUp() throws Exception {
        userRepository = new UserRepository();
        loginFlow = new LoginFlow();
    }

    @Test
    public void signIn_ValidCredentials_loginPossible() {
        User validUser = userRepository.getValidUser();

        loginFlow.doLogin(validUser.getCredentials());

        assertThat(loginFlow.userLoggedIn(), is(true));
    }

    @Test
    public void signIn_UsernameTooShort_loginNotPossible() {
        User usernameTooShortUser = userRepository.getUserWithTooShortUsername();

        loginFlow.doLogin(usernameTooShortUser.getCredentials());

        assertThat(loginFlow.correctErrorDialogIsShown(PASSWORD_USERNAME_TOO_SHORT), is(true));
    }

    @Test
    public void signIn_PasswordTooShort_loginNotPossible() {
        User passwordTooShortUser = userRepository.getUserWithTooShortPassword();

        loginFlow.doLogin(passwordTooShortUser.getCredentials());

        assertThat(loginFlow.correctErrorDialogIsShown(PASSWORD_USERNAME_TOO_SHORT), is(true));
    }

    @After
    public void tearDown() throws Exception {
        Application.setSignedOut();
    }
}
