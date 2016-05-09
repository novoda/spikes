package com.novoda.firechat.chat.presenter;

import com.novoda.firechat.chat.displayer.ChatDisplayer;
import com.novoda.firechat.chat.model.Chat;
import com.novoda.firechat.chat.model.Message;
import com.novoda.firechat.chat.service.ChatService;

import rx.Subscription;
import rx.functions.Action1;

public class ChatPresenter {

    private final ChatService chatService;
    private final ChatDisplayer chatDisplayer;

    private Subscription subscription;

    public ChatPresenter(ChatService chatService, ChatDisplayer chatDisplayer) {
        this.chatService = chatService;
        this.chatDisplayer = chatDisplayer;
    }

    public void startPresenting() {
        chatDisplayer.attach(actionListener);
        subscription = chatService.getChat().subscribe(new Action1<Chat>() { //TODO sort out error flow
            @Override
            public void call(Chat chat) {
                chatDisplayer.display(chat);
            }
        });
    }

    public void stopPresenting() {
        chatDisplayer.detach(actionListener);
        subscription.unsubscribe(); //TODO sort out checks
    }

    private final ChatDisplayer.ChatActionListener actionListener = new ChatDisplayer.ChatActionListener() {
        @Override
        public void onSubmitMessage(Message message) {
            chatService.sendMessage(message);
        }
    };

}
