package com.novoda.bonfire;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.novoda.bonfire.analytics.FirebaseAnalyticsAnalytics;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.channel.service.FirebaseChannelService;
import com.novoda.bonfire.chat.service.ChatService;
import com.novoda.bonfire.chat.service.FirebaseChatService;
import com.novoda.bonfire.login.service.FirebaseLoginService;
import com.novoda.bonfire.login.service.LoginService;

public enum Dependencies {
    INSTANCE;

    private FirebaseAnalyticsAnalytics firebaseAnalytics;

    private LoginService loginService;
    private ChatService chatService;
    private ChannelService channelService;

    public void init(Context context) {
        if (needsInitialisation()) {
            Context appContext = context.getApplicationContext();
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(appContext, FirebaseOptions.fromResource(appContext), "Bonfire");
            firebaseAnalytics = new FirebaseAnalyticsAnalytics(context);
            loginService = new FirebaseLoginService(firebaseApp);
            chatService = new FirebaseChatService(firebaseApp);
            channelService = new FirebaseChannelService(firebaseApp);
        }
    }

    private boolean needsInitialisation() {
        return loginService == null && chatService == null;
    }

    public FirebaseAnalyticsAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public ChatService getChatService() {
        return chatService;
    }

    public ChannelService getChannelService() {
        return channelService;
    }
}
