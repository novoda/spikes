package com.novoda.firechat.chat.presenter;

import com.novoda.firechat.chat.displayer.ChatDisplayer;
import com.novoda.firechat.login.service.LoginService;
import com.novoda.firechat.login.data.model.User;
import com.novoda.firechat.chat.data.model.Chat;
import com.novoda.firechat.chat.data.model.Message;
import com.novoda.firechat.chat.service.ChatService;

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
                    public void call(Chat chat) {
                        chatDisplayer.display(chat);
                    }
                })
        );
        subscriptions.add(
                loginService.getUser().subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        ChatPresenter.this.user = user;
                        chatDisplayer.enableInteraction();
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
        public void onSubmitMessage(String message) {
            chatService.sendMessage(new Message(user.getName(), message));
        }
    };

}
