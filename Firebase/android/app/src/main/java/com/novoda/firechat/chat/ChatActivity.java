package com.novoda.firechat.chat;

import android.os.Bundle;

import com.novoda.firechat.BaseActivity;
import com.novoda.firechat.R;
import com.novoda.firechat.chat.displayer.ChatDisplayer;
import com.novoda.firechat.chat.presenter.ChatPresenter;

public class ChatActivity extends BaseActivity {

    private ChatPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ChatDisplayer chatDisplayer = (ChatDisplayer) findViewById(R.id.chatView);
        presenter = new ChatPresenter(getFireChatApplication().getLoginService(), getFireChatApplication().getChatService(), chatDisplayer);
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
