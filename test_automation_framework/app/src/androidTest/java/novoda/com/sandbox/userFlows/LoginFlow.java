package novoda.com.sandbox.userFlows;

import novoda.com.sandbox.models.Credentials;
import novoda.com.sandbox.pages.MainPage;
import novoda.com.sandbox.pages.SignInPage;

public class LoginFlow {

    private MainPage mainView;
    private SignInPage signInPage;

    public LoginFlow() {
        mainView = new MainPage();
        signInPage = new SignInPage();
    }

    public void doLogin(Credentials credentials) {
        mainView.openSignInPage();
        signInPage.doLogin(credentials);
    }

    public boolean userLoggedIn() {
        mainView.validateLoggedInStatus();
        return true;
    }


    public boolean correctErrorDialogIsShown(String expectedError) {
        signInPage.validateErrorDialog(expectedError);
        return true;
    }
}
