package com.novoda.bonfire.welcome.presenter;

import com.novoda.bonfire.analytics.Analytics;
import com.novoda.bonfire.navigation.Navigator;
import com.novoda.bonfire.welcome.displayer.WelcomeDisplayer;
import com.novoda.bonfire.welcome.displayer.WelcomeDisplayer.InteractionListener;

public class WelcomePresenter {

    private final WelcomeDisplayer welcomeDisplayer;
    private final Navigator navigator;
    private final Analytics analytics;
    private final String sender;

    public WelcomePresenter(WelcomeDisplayer welcomeDisplayer, Navigator navigator, Analytics analytics, String sender) {
        this.welcomeDisplayer = welcomeDisplayer;
        this.navigator = navigator;
        this.analytics = analytics;
        this.sender = sender;
    }

    public void startPresenting() {
        welcomeDisplayer.attach(interactionListener);
        welcomeDisplayer.display(sender);
        analytics.trackInvitationOpened(sender);
    }

    public void stopPresenting() {
        welcomeDisplayer.detach(interactionListener);
    }

    private final InteractionListener interactionListener = new InteractionListener() {
        @Override
        public void onGetStartedClicked() {
            analytics.trackInvitationAccepted(sender);
            navigator.toLogin();
        }
    };
}
