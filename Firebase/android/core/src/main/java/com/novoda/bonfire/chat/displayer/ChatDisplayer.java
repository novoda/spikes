package com.novoda.bonfire.chat.displayer;

import com.novoda.bonfire.chat.data.model.Chat;

public interface ChatDisplayer {

    void attach(ChatActionListener actionListener);

    void detach(ChatActionListener actionListener);

    void display(Chat chat);

    void enableInteraction();

    void disableInteraction();

    interface ChatActionListener {

        void onMessageLengthChanged(int messageLength);

        void onSubmitMessage(String message);

    }

}
