package com.novoda.bonfire.chat.presenter;

import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.chat.displayer.ChatDisplayer;
import com.novoda.bonfire.chat.service.ChatService;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.data.model.User;
import com.novoda.bonfire.login.service.LoginService;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class ChatPresenter {

    private final LoginService loginService;
    private final ChatService chatService;
    private final ChatDisplayer chatDisplayer;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    private User user; //TODO replace nullable by optional ?

    public ChatPresenter(LoginService loginService, ChatService chatService, ChatDisplayer chatDisplayer) {
        this.loginService = loginService;
        this.chatService = chatService;
        this.chatDisplayer = chatDisplayer;
    }

    public void startPresenting() {
        chatDisplayer.attach(actionListener);
        chatDisplayer.disableInteraction();
        subscriptions.add(
                chatService.getChat().subscribe(new Action1<Chat>() { //TODO sort out error flow
                    @Override
                    public void call(com.novoda.bonfire.chat.data.model.Chat chat) {
                        chatDisplayer.display(chat);
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

    private final ChatDisplayer.ChatActionListener actionListener = new ChatDisplayer.ChatActionListener() {
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
            chatService.sendMessage(new Message(user, message));
        }
    };

    private boolean userIsAuthenticated() {
        return user != null;
    }

}
