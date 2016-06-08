package com.novoda.bonfire.login;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.common.SignInButton;
import com.novoda.bonfire.R;
import com.novoda.bonfire.TestDependencies;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.bonfire.user.data.model.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> activity = new ActivityTestRule<>(LoginActivity.class, false, false);
    private Authentication authentication;

    @Before
    public void setUp() throws Exception {
        LoginService loginService = Mockito.mock(LoginService.class);
        authentication = Mockito.mock(Authentication.class);
        when(loginService.getAuthentication()).thenReturn(Observable.just(authentication));

        TestDependencies.init()
                .withLoginService(loginService);
    }

    @Test
    public void signInButtonIsVisibleWhenAuthenticationFails() {
        givenAuthenticationFails();

        activity.launchActivity(new Intent());

        assertThatGoogleSignInButtonIsShown();
    }

    private void givenAuthenticationFails() {
        when(authentication.isSuccess()).thenReturn(false);
        when(authentication.getFailure()).thenReturn(new Throwable("Message"));
    }

    private void assertThatGoogleSignInButtonIsShown() {
        onView(withClassName(endsWith(SignInButton.class.getSimpleName()))).check(matches(isDisplayed()));
    }

    @Test
    public void whenAuthenticationSucceedsThenListOfChannelsIsShownImmediately() throws Exception {
        givenAuthenticationIsSuccessful();

        activity.launchActivity(new Intent());

        assertThatScreenWIthChannelsIsShown();
    }

    private void givenAuthenticationIsSuccessful() {
        when(authentication.isSuccess()).thenReturn(true);
        when(authentication.getUser()).thenReturn(new User("id", "name", "http://photo.url"));
    }

    private ViewInteraction assertThatScreenWIthChannelsIsShown() {
        return onView(withId(R.id.channels)).check(matches(isDisplayed()));
    }

}
