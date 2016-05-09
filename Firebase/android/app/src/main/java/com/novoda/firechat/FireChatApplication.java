package com.novoda.firechat;

import android.app.Application;

import com.novoda.firechat.chat.login.LoginService;
import com.novoda.firechat.chat.service.ChatService;
import com.novoda.firechat.chat.service.FirebaseChatService;
import com.novoda.firechat.login.service.SharedPrefsLoginService;

public class FireChatApplication extends Application {

    private LoginService loginService;
    private ChatService chatService;

    @Override
    public void onCreate() {
        super.onCreate();
        chatService = new FirebaseChatService(this);
        loginService = new SharedPrefsLoginService(this);
    }

    public ChatService getChatService() {
        return chatService;
    }

    public LoginService getLoginService() {
        return loginService;
    }
}
