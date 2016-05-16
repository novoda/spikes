package com.novoda.bonfire.chat.service;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;

import rx.Observable;

public interface ChatService {

    Observable<Chat> getChat(Channel channel);

    void sendMessage(Channel channel, Message message);

}
