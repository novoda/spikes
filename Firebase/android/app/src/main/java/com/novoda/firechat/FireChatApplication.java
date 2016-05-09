package com.novoda.firechat;

import android.app.Application;

import com.novoda.firechat.chat.service.ChatService;
import com.novoda.firechat.chat.service.FirebaseChatService;

public class FireChatApplication extends Application {

    private ChatService chatService;

    @Override
    public void onCreate() {
        super.onCreate();
        chatService = new FirebaseChatService(this);
    }

    public ChatService getChatService() {
        return chatService;
    }

}
