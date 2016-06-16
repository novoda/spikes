package com.novoda.bonfire.chat.presenter;

import com.novoda.bonfire.analytics.Analytics;
import com.novoda.bonfire.analytics.ErrorLogger;
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

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

import static com.novoda.bonfire.chat.presenter.ChatPresenter.Pair.asPair;

public class ChatPresenter {

    private final LoginService loginService;
    private final ChatService chatService;
    private final ChatDisplayer chatDisplayer;
    private final Analytics analytics;
    private final Channel channel;
    private final Navigator navigator;
    private final ErrorLogger errorLogger;

    private CompositeSubscription subscriptions = new CompositeSubscription();
    private User user;

    public ChatPresenter(
            LoginService loginService,
            ChatService chatService,
            ChatDisplayer chatDisplayer,
            Channel channel,
            Analytics analytics,
            Navigator navigator,
            ErrorLogger errorLogger
    ) {
        this.loginService = loginService;
        this.chatService = chatService;
        this.chatDisplayer = chatDisplayer;
        this.analytics = analytics;
        this.channel = channel;
        this.navigator = navigator;
        this.errorLogger = errorLogger;
    }

    public void startPresenting() {
        chatDisplayer.setTitle(channel.getName());
        if (channel.isPrivate()) {
            chatDisplayer.showAddMembersButton();
        }
        chatDisplayer.attach(actionListener);
        chatDisplayer.disableInteraction();
        subscriptions.add(
                Observable.combineLatest(chatService.getChat(channel), loginService.getAuthentication(), asPair())
                        .subscribe(new Action1<Pair>() {
                            @Override
                            public void call(Pair pair) {
                                if (pair.auth.isSuccess()) {
                                    user = pair.auth.getUser();
                                    displayChat(pair);
                                } else {
                                    errorLogger.reportError(pair.auth.getFailure(), "Not logged in when opening chat");
                                    navigator.toLogin();
                                }
                            }
                        })
        );
    }

    private void displayChat(ChatPresenter.Pair pair) {
        if (pair.chatResult.isSuccess()) {
            chatDisplayer.display(pair.chatResult.getData(), user);
        } else {
            errorLogger.reportError(pair.chatResult.getFailure(), "Cannot open chat");
            navigator.toChannels();
        }
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
            analytics.trackMessageLength(message.length(), user.getId(), channel.getName());
        }

        @Override
        public void onManageOwnersClicked() {
            analytics.trackManageOwners(user.getId(), channel.getName());
            navigator.toMembersOf(channel);
        }
    };

    static class Pair {

        public final DatabaseResult<Chat> chatResult;
        public final Authentication auth;

        private Pair(DatabaseResult<Chat> chatResult, Authentication auth) {
            this.chatResult = chatResult;
            this.auth = auth;
        }

        static Func2<DatabaseResult<Chat>, Authentication, Pair> asPair() {
            return new Func2<DatabaseResult<Chat>, Authentication, Pair>() {
                @Override
                public ChatPresenter.Pair call(DatabaseResult<Chat> chatDatabaseResult, Authentication authentication) {
                    return new ChatPresenter.Pair(chatDatabaseResult, authentication);
                }
            };
        }

    }

}
