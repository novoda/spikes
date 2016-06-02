package com.novoda.bonfire.chat.service;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.database.DatabaseResult;

import rx.Observable;

public interface ChatService {

    Observable<DatabaseResult<Chat>> getChat(Channel channel);

    void sendMessage(Channel channel, Message message);

}
