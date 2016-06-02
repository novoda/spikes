package com.novoda.bonfire.welcome.displayer;

public interface WelcomeDisplayer {

    void attach(InteractionListener interactionListener);

    void detach(InteractionListener interactionListener);

    void display(String sender);

    interface InteractionListener {
        void onGetStartedClicked();
    }
}
