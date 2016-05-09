package com.novoda.firechat.chat.displayer;

import com.novoda.firechat.chat.model.Chat;
import com.novoda.firechat.chat.model.Message;

public interface ChatDisplayer {

    void attach(ChatActionListener actionListener);

    void detach(ChatActionListener actionListener);

    void display(Chat chat);

    public interface ChatActionListener {

        void onSubmitMessage(Message message);

    }

}
