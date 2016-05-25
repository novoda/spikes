package com.novoda.bonfire.chat.service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.database.DatabaseResult;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.BooleanSubscription;

public class FirebaseChatService implements ChatService {

    private final DatabaseReference messagesDB;

    public FirebaseChatService(FirebaseDatabase firebaseDatabase) {
        messagesDB = firebaseDatabase.getReference("messages");
    }

    @Override
    public Observable<DatabaseResult<Chat>> getChat(final Channel channel) {
        return Observable.create(new Observable.OnSubscribe<DatabaseResult<Chat>>() {
            @Override
            public void call(final Subscriber<? super DatabaseResult<Chat>> subscriber) {
                final ValueEventListener eventListener = messagesInChannel(channel).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Message> messages = toMessages(dataSnapshot);
                        subscriber.onNext(new DatabaseResult<>(new Chat(messages)));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        subscriber.onNext(new DatabaseResult<Chat>(databaseError.toException()));
                    }

                });
                subscriber.add(BooleanSubscription.create(new Action0() {
                    @Override
                    public void call() {
                        messagesInChannel(channel).removeEventListener(eventListener);
                    }
                }));
            }
        });
    }

    @Override
    public void sendMessage(Channel channel, Message message) {
        messagesInChannel(channel).push().setValue(message); //TODO handle errors
    }

    private DatabaseReference messagesInChannel(Channel channel) {
        return messagesDB.child(channel.getName());
    }

    private List<Message> toMessages(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        List<Message> messages = new ArrayList<>();
        for (DataSnapshot child : children) {
            Message message = child.getValue(Message.class);
            messages.add(message);
        }
        return messages;
    }

}
