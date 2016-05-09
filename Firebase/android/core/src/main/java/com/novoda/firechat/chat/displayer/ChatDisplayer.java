package com.novoda.firechat.chat.displayer;

import com.novoda.firechat.chat.model.Chat;

public interface ChatDisplayer {

    void attach(ChatActionListener actionListener);

    void detach(ChatActionListener actionListener);

    void display(Chat chat);

    void enableInteraction();

    void disableInteraction();

    public interface ChatActionListener {

        void onSubmitMessage(String message);

    }

}
