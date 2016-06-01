package com.novoda.bonfire.chat.presenter;

import com.novoda.bonfire.analytics.Analytics;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.chat.displayer.ChatDisplayer;
import com.novoda.bonfire.chat.service.ChatService;
import com.novoda.bonfire.database.DatabaseResult;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.bonfire.navigation.Navigator;
import com.novoda.bonfire.user.data.model.User;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class ChatPresenter {

    private final LoginService loginService;
    private final ChatService chatService;
    private final ChatDisplayer chatDisplayer;
    private final Analytics analytics;
    private final Channel channel;
    private final Navigator navigator;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    private User user;

    public ChatPresenter(LoginService loginService, ChatService chatService, ChatDisplayer chatDisplayer, Channel channel, Analytics analytics, Navigator navigator) {
        this.loginService = loginService;
        this.chatService = chatService;
        this.chatDisplayer = chatDisplayer;
        this.analytics = analytics;
        this.channel = channel;
        this.navigator = navigator;
    }

    public void startPresenting() {
        chatDisplayer.setTitle(channel.getName());
        if (channel.isPrivate()) {
            chatDisplayer.showAddMembersButton();
        }
        chatDisplayer.attach(actionListener);
        chatDisplayer.disableInteraction();
        subscriptions.add(
                chatService.getChat(channel).subscribe(new Action1<DatabaseResult<Chat>>() {
                    @Override
                    public void call(DatabaseResult<Chat> result) {
                        if (result.isSuccess()) {
                            chatDisplayer.display(result.getData());
                        } else {
                            navigator.toChannels();
                        }
                    }
                })
        );
        subscriptions.add(
                loginService.getAuthentication().subscribe(new Action1<Authentication>() {
                    @Override
                    public void call(Authentication authentication) {
                        ChatPresenter.this.user = authentication.getUser(); //TODO handle missing auth
                    }
                })
        );
    }

    public void stopPresenting() {
        chatDisplayer.detach(actionListener);
        subscriptions.clear(); //TODO sort out checks
        subscriptions = new CompositeSubscription();
    }

    private boolean userIsAuthenticated() {
        return user != null;
    }

    private final ChatDisplayer.ChatActionListener actionListener = new ChatDisplayer.ChatActionListener() {
        @Override
        public void onUpPressed() {
            navigator.toParent();
        }

        @Override
        public void onMessageLengthChanged(int messageLength) {
            if (userIsAuthenticated() && messageLength > 0) {
                chatDisplayer.enableInteraction();
            } else {
                chatDisplayer.disableInteraction();
            }
        }

        @Override
        public void onSubmitMessage(String message) {
            chatService.sendMessage(channel, new Message(user, message));
            analytics.trackEvent("message_length", message.length());
        }

        @Override
        public void onManageOwnersClicked() {
            navigator.toMembersOf(channel);
        }
    };

}
