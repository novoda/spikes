package com.novoda.bonfire.welcome.presenter;

import com.novoda.bonfire.navigation.Navigator;
import com.novoda.bonfire.welcome.displayer.WelcomeDisplayer;
import com.novoda.bonfire.welcome.displayer.WelcomeDisplayer.InteractionListener;

public class WelcomePresenter {

    private final WelcomeDisplayer welcomeDisplayer;
    private final Navigator navigator;
    private final String sender;

    public WelcomePresenter(WelcomeDisplayer welcomeDisplayer, Navigator navigator, String sender) {
        this.welcomeDisplayer = welcomeDisplayer;
        this.navigator = navigator;
        this.sender = sender;
    }

    public void startPresenting() {
        welcomeDisplayer.attach(interactionListener);
        welcomeDisplayer.display(sender);
    }

    public void stopPresenting() {
        welcomeDisplayer.detach(interactionListener);
    }

    private final InteractionListener interactionListener = new InteractionListener() {
        @Override
        public void onGetStartedClicked() {
            navigator.toLogin();
        }
    };
}
