package com.novoda.bonfire.chat.service;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.chat.database.ChatDatabase;
import com.novoda.bonfire.database.DatabaseResult;

import rx.Observable;
import rx.functions.Func1;

public class PersistedChatService implements ChatService {

    private final ChatDatabase chatDatabase;

    public PersistedChatService(ChatDatabase chatDatabase) {
        this.chatDatabase = chatDatabase;
    }

    @Override
    public Observable<DatabaseResult<Chat>> getChat(final Channel channel) {
        return chatDatabase.observeChat(channel)
                .map(asDatabaseResult())
                .onErrorReturn(DatabaseResult.<Chat>errorAsDatabaseResult());
    }

    private static Func1<Chat, DatabaseResult<Chat>> asDatabaseResult() {
        return new Func1<Chat, DatabaseResult<Chat>>() {
            @Override
            public DatabaseResult<Chat> call(Chat chat) {
                return new DatabaseResult<Chat>(chat);
            }
        };
    }

    @Override
    public void sendMessage(Channel channel, Message message) {
        chatDatabase.sendMessage(channel, message);
    }

}
