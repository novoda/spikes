package com.novoda.bonfire.chat.service;

import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;

import rx.Observable;

public interface ChatService {

    Observable<Chat> getChat();

    void sendMessage(Message message);

}
