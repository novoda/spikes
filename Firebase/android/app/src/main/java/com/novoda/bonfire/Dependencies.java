package com.novoda.bonfire;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.novoda.bonfire.analytics.FirebaseAnalyticsAnalytics;
import com.novoda.bonfire.chat.service.ChatService;
import com.novoda.bonfire.chat.service.FirebaseChatService;
import com.novoda.bonfire.login.service.FirebaseLoginService;
import com.novoda.bonfire.login.service.LoginService;

public enum Dependencies {
    INSTANCE;

    private LoginService loginService;
    private ChatService chatService;
    private FirebaseAnalyticsAnalytics firebaseAnalytics;

    public void init(Context context) {
        if (needsInitialisation()) {
            Context appContext = context.getApplicationContext();
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(appContext, FirebaseOptions.fromResource(appContext), "Bonfire");
            chatService = new FirebaseChatService(firebaseApp);
            loginService = new FirebaseLoginService(firebaseApp);
            firebaseAnalytics = new FirebaseAnalyticsAnalytics(context);
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

    public FirebaseAnalyticsAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }
}
