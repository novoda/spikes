package com.novoda.bonfire.welcome.displayer;

import com.novoda.bonfire.user.data.model.User;

public interface WelcomeDisplayer {

    void attach(InteractionListener interactionListener);

    void detach(InteractionListener interactionListener);

    void display(User sender);

    interface InteractionListener {
        void onGetStartedClicked();
    }
}
