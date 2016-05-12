package com.novoda.bonfire.chat;

import android.os.Bundle;

import com.novoda.bonfire.BaseActivity;
import com.novoda.bonfire.BonfireApplication;
import com.novoda.bonfire.R;
import com.novoda.bonfire.chat.presenter.ChatPresenter;

public class ChatActivity extends BaseActivity {

    private com.novoda.bonfire.chat.presenter.ChatPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        com.novoda.bonfire.chat.displayer.ChatDisplayer chatDisplayer = (com.novoda.bonfire.chat.displayer.ChatDisplayer) findViewById(R.id.chatView);
        BonfireApplication application = getFireChatApplication();
        presenter = new ChatPresenter(application.getLoginService(), application.getChatService(), chatDisplayer, application.getAnalytics());
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
