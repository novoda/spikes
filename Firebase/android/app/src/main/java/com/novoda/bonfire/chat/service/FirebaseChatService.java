package com.novoda.bonfire.chat.service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.database.DatabaseResult;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.novoda.bonfire.rx.ValueEventObservable.listenToValueEvents;

public class FirebaseChatService implements ChatService {

    private final DatabaseReference messagesDB;

    public FirebaseChatService(FirebaseDatabase firebaseDatabase) {
        messagesDB = firebaseDatabase.getReference("messages");
    }

    @Override
    public Observable<DatabaseResult<Chat>> getChat(final Channel channel) {
        return listenToValueEvents(messagesInChannel(channel), toChat())
                .onErrorReturn(DatabaseResult.<Chat>errorAsDatabaseResult());
    }

    @Override
    public void sendMessage(Channel channel, Message message) {
        messagesInChannel(channel).push().setValue(message); //TODO handle errors
    }

    private DatabaseReference messagesInChannel(Channel channel) {
        return messagesDB.child(channel.getName());
    }

    private Func1<DataSnapshot, DatabaseResult<Chat>> toChat() {
        return new Func1<DataSnapshot, DatabaseResult<Chat>>() {
            @Override
            public DatabaseResult<Chat> call(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot child : children) {
                    Message message = child.getValue(Message.class);
                    messages.add(message);
                }
                return new DatabaseResult<Chat>(new Chat(messages));
            }
        };
    }

}
