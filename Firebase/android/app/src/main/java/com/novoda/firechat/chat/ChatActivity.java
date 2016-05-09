package com.novoda.firechat.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.novoda.firechat.FireChatApplication;
import com.novoda.firechat.R;
import com.novoda.firechat.chat.displayer.ChatDisplayer;
import com.novoda.firechat.chat.presenter.ChatPresenter;

public class ChatActivity extends AppCompatActivity {

    private ChatPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        presenter = new ChatPresenter(((FireChatApplication) getApplication()).getChatService(), (ChatDisplayer) findViewById(R.id.chatView));
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
