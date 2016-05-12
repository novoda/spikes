package com.novoda.bonfire;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.novoda.bonfire.chat.service.ChatService;
import com.novoda.bonfire.chat.service.FirebaseChatService;
import com.novoda.bonfire.login.service.FirebaseLoginService;
import com.novoda.bonfire.login.service.LoginService;

public enum Dependencies {
    INSTANCE;

    private LoginService loginService;
    private ChatService chatService;

    public void init(Context context) {
        if (needsInitialisation()) {
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(context, FirebaseOptions.fromResource(context), "Bonfire");
            chatService = new FirebaseChatService(firebaseApp);
            loginService = new FirebaseLoginService(firebaseApp);
        }
    }

    private boolean needsInitialisation() {
        return loginService == null && chatService == null;
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public ChatService getChatService() {
        return chatService;
    }

}
