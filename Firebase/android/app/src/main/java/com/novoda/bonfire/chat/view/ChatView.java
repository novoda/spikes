package com.novoda.bonfire.chat.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.bonfire.R;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.displayer.ChatDisplayer;
import com.novoda.notils.caster.Views;

public class ChatView extends LinearLayout implements ChatDisplayer {

    private TextView messageView;
    private ChatAdapter chatAdapter;
    private View submitButton;
    private RecyclerView recyclerView;

    private ChatActionListener actionListener;
    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_chat_view, this);
        chatAdapter = new ChatAdapter();
        messageView = Views.findById(this, R.id.messageEdit);
        submitButton = Views.findById(this, R.id.submitButton);
        recyclerView = Views.findById(this, R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    @Override
    public void attach(final ChatActionListener actionListener) {
        this.actionListener = actionListener;
        messageView.addTextChangedListener(textWatcher);
        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionListener.onSubmitMessage(messageView.getText().toString());
                messageView.setText("");
            }
        });
    }

    @Override
    public void detach(ChatActionListener actionListener) {
        submitButton.setOnClickListener(null);
        messageView.removeTextChangedListener(textWatcher);
        this.actionListener = null;
    }

    @Override
    public void display(Chat chat) {
        chatAdapter.update(chat);
        int lastMessagePosition = chatAdapter.getItemCount() == 0 ? 0 : chatAdapter.getItemCount() - 1;
        recyclerView.smoothScrollToPosition(lastMessagePosition);
    }

    @Override
    public void enableInteraction() {
        submitButton.setEnabled(true);
    }

    @Override
    public void disableInteraction() {
        submitButton.setEnabled(false);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            actionListener.onMessageLengthChanged(s.length());
        }
    };

}
