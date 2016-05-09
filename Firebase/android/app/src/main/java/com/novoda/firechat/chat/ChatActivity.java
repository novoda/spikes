package com.novoda.firechat.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.novoda.firechat.FireChatApplication;
import com.novoda.firechat.R;
import com.novoda.firechat.chat.displayer.ChatDisplayer;
import com.novoda.firechat.chat.presenter.ChatPresenter;
import com.novoda.firechat.chat.service.ChatService;

public class ChatActivity extends AppCompatActivity {

    private ChatPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ChatService chatService = ((FireChatApplication) getApplication()).getChatService();
        ChatDisplayer chatDisplayer = (ChatDisplayer) findViewById(R.id.chatView);
        presenter = new ChatPresenter(chatService, chatDisplayer);
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
