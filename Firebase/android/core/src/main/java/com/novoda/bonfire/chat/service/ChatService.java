package com.novoda.bonfire.chat.service;

import com.novoda.bonfire.chat.data.model.Message;

import rx.Observable;

public interface ChatService {

    Observable<com.novoda.bonfire.chat.data.model.Chat> getChat();

    void sendMessage(Message message);

}
