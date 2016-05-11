package com.novoda.bonfire.chat.displayer;

public interface ChatDisplayer {

    void attach(ChatActionListener actionListener);

    void detach(ChatActionListener actionListener);

    void display(com.novoda.bonfire.chat.data.model.Chat chat);

    void enableInteraction();

    void disableInteraction();

    public interface ChatActionListener {

        void onSubmitMessage(String message);

    }

}
