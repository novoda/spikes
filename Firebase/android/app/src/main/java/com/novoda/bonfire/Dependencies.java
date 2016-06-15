package com.novoda.bonfire;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.analytics.Analytics;
import com.novoda.bonfire.analytics.ErrorLogger;
import com.novoda.bonfire.analytics.FirebaseAnalyticsAnalytics;
import com.novoda.bonfire.analytics.FirebaseErrorLogger;
import com.novoda.bonfire.channel.database.FirebaseChannelsDatabase;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.channel.service.PersistedChannelService;
import com.novoda.bonfire.chat.database.FirebaseChatDatabase;
import com.novoda.bonfire.chat.service.ChatService;
import com.novoda.bonfire.chat.service.PersistedChatService;
import com.novoda.bonfire.login.database.FirebaseAuthDatabase;
import com.novoda.bonfire.login.service.FirebaseLoginService;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.bonfire.rx.FirebaseObservableListeners;
import com.novoda.bonfire.user.database.FirebaseUserDatabase;
import com.novoda.bonfire.user.service.PersistedUserService;
import com.novoda.bonfire.user.service.UserService;

public enum Dependencies {
    INSTANCE;

    private Analytics analytics;
    private ErrorLogger errorLogger;

    private LoginService loginService;
    private ChatService chatService;
    private ChannelService channelService;
    private UserService userService;
    private Config config;

    public void init(Context context) {
        if (needsInitialisation()) {
            Context appContext = context.getApplicationContext();
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(appContext, FirebaseOptions.fromResource(appContext), "Bonfire");
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
            firebaseDatabase.setPersistenceEnabled(true);
            FirebaseObservableListeners firebaseObservableListeners = new FirebaseObservableListeners();
            FirebaseUserDatabase userDatabase = new FirebaseUserDatabase(firebaseDatabase, firebaseObservableListeners);

            analytics = new FirebaseAnalyticsAnalytics(FirebaseAnalytics.getInstance(appContext));
            errorLogger = new FirebaseErrorLogger();
            loginService = new FirebaseLoginService(new FirebaseAuthDatabase(firebaseAuth), userDatabase);
            chatService = new PersistedChatService(new FirebaseChatDatabase(firebaseDatabase, firebaseObservableListeners));
            channelService = new PersistedChannelService(new FirebaseChannelsDatabase(firebaseDatabase, firebaseObservableListeners), userDatabase);
            userService = new PersistedUserService(userDatabase);
            config = FirebaseConfig.newInstance().init(errorLogger);
        }
    }

    private boolean needsInitialisation() {
        return loginService == null || chatService == null || channelService == null
                || userService == null || analytics == null || errorLogger == null;
    }

    public Analytics getAnalytics() {
        return analytics;
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

    public UserService getUserService() {
        return userService;
    }

    public ErrorLogger getErrorLogger() {
        return errorLogger;
    }

    public Config getConfig() {
        return config;
    }
}
