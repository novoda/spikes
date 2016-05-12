package com.novoda.bonfire.chat;

import android.os.Bundle;

import com.novoda.bonfire.BaseActivity;
import com.novoda.bonfire.Dependencies;
import com.novoda.bonfire.R;
import com.novoda.bonfire.chat.displayer.ChatDisplayer;
import com.novoda.bonfire.chat.presenter.ChatPresenter;

public class ChatActivity extends BaseActivity {

    private ChatPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ChatDisplayer chatDisplayer = (ChatDisplayer) findViewById(R.id.chatView);
        Dependencies dependencies = Dependencies.INSTANCE;
        presenter = new ChatPresenter(dependencies.getLoginService(), dependencies.getChatService(), chatDisplayer, dependencies.getFirebaseAnalytics());
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startPresenting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopPresenting();
    }

}
