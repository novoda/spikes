package com.novoda.bonfire;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.novoda.bonfire.analytics.FirebaseAnalyticsAnalytics;
import com.novoda.bonfire.login.service.FirebaseLoginService;
import com.novoda.bonfire.chat.service.ChatService;
import com.novoda.bonfire.chat.service.FirebaseChatService;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.notils.logger.simple.Log;

public class BonfireApplication extends Application {

    private LoginService loginService;
    private ChatService chatService;
    private FirebaseAnalyticsAnalytics firebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.setShowLogs(BuildConfig.DEBUG);
        Log.e("XXX", "on create");
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(this, FirebaseOptions.fromResource(this), "Bonfire");
        chatService = new FirebaseChatService(firebaseApp);
        loginService = new FirebaseLoginService(firebaseApp);
        firebaseAnalytics = new FirebaseAnalyticsAnalytics(this);
    }

    public ChatService getChatService() {
        return chatService;
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public FirebaseAnalyticsAnalytics getAnalytics() {
        return firebaseAnalytics;
    }
}
