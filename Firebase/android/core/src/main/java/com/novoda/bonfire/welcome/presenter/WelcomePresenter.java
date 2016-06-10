package com.novoda.bonfire.welcome.presenter;

import com.novoda.bonfire.analytics.Analytics;
import com.novoda.bonfire.navigation.Navigator;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.service.UserService;
import com.novoda.bonfire.welcome.displayer.WelcomeDisplayer;
import com.novoda.bonfire.welcome.displayer.WelcomeDisplayer.InteractionListener;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class WelcomePresenter {

    private final UserService userService;
    private final WelcomeDisplayer welcomeDisplayer;
    private final Navigator navigator;
    private final Analytics analytics;
    private final String senderId;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public WelcomePresenter(UserService userService, WelcomeDisplayer welcomeDisplayer, Navigator navigator, Analytics analytics, String senderId) {
        this.userService = userService;
        this.welcomeDisplayer = welcomeDisplayer;
        this.navigator = navigator;
        this.analytics = analytics;
        this.senderId = senderId;
    }

    public void startPresenting() {
        welcomeDisplayer.attach(interactionListener);
        analytics.trackInvitationOpened(senderId);
        subscriptions.add(
                userService.getUser(senderId).subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        welcomeDisplayer.display(user);
                    }
                })
        );
    }

    public void stopPresenting() {
        welcomeDisplayer.detach(interactionListener);
        subscriptions.clear(); //TODO sort out checks
        subscriptions = new CompositeSubscription();
    }

    private final InteractionListener interactionListener = new InteractionListener() {
        @Override
        public void onGetStartedClicked() {
            analytics.trackInvitationAccepted(senderId);
            navigator.toLogin();
        }
    };
}
