package com.novoda.firechat.chat.service;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.novoda.firechat.chat.data.model.Chat;
import com.novoda.firechat.chat.data.model.Message;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.BooleanSubscription;

public class FirebaseChatService implements ChatService {

    private final Firebase firebase;

    public FirebaseChatService(Context context) {
        Firebase.setAndroidContext(context);
        firebase = new Firebase("https://firechat-novoda.firebaseio.com/");
    }

    @Override
    public Observable<Chat> getChat() {
        return Observable.create(new Observable.OnSubscribe<Chat>() {
            @Override
            public void call(final Subscriber<? super Chat> subscriber) {
                final ValueEventListener eventListener = firebase.child("messages").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Message> messages = toMessages(dataSnapshot);
                        subscriber.onNext(new Chat(messages));
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        subscriber.onError(firebaseError.toException());
                    }
                });
                subscriber.add(BooleanSubscription.create(new Action0() {
                    @Override
                    public void call() {
                        firebase.removeEventListener(eventListener);
                    }
                }));
            }
        });
    }

    @Override
    public void sendMessage(Message message) {
        firebase.child("messages").push().setValue(message);
    }

    private List<Message> toMessages(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        List<Message> messages = new ArrayList<Message>();
        for (DataSnapshot child : children) {
            Message message = child.getValue(Message.class);
            messages.add(message);
        }
        return messages;
    }

}
