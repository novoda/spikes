package com.novoda.bonfire.chat.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.rx.FirebaseObservableListeners;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class FirebaseChatDatabase implements ChatDatabase {

    private static final int DEFAULT_LIMIT = 1000;

    private final DatabaseReference messagesDB;
    private final FirebaseObservableListeners firebaseObservableListeners;

    public FirebaseChatDatabase(FirebaseDatabase firebaseDatabase, FirebaseObservableListeners firebaseObservableListeners) {
        messagesDB = firebaseDatabase.getReference("messages");
        this.firebaseObservableListeners = firebaseObservableListeners;
    }

    @Override
    public Observable<Chat> observeChat(Channel channel) {
        return firebaseObservableListeners.listenToValueEvents(messagesInChannel(channel).limitToLast(DEFAULT_LIMIT), toChat());
    }

    @Override
    public void sendMessage(Channel channel, Message message) {
        messagesInChannel(channel).push().setValue(message); //TODO handle errors
    }

    private DatabaseReference messagesInChannel(Channel channel) {
        return messagesDB.child(channel.getName());
    }

    private Func1<DataSnapshot, Chat> toChat() {
        return new Func1<DataSnapshot, Chat>() {
            @Override
            public Chat call(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot child : children) {
                    Message message = child.getValue(Message.class);
                    messages.add(message);
                }
                return new Chat(messages);
            }
        };
    }

}
