package com.novoda.dungeoncrawler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

class RemoteGamePauseObservable {

    private final FirebaseDatabase database;
    private final OnToggleListener onToggleListener;

    RemoteGamePauseObservable(FirebaseDatabase database, OnToggleListener onToggleListener) {
        this.database = database;
        this.onToggleListener = onToggleListener;
    }

    private final ValueEventListener toggleListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (!dataSnapshot.exists()) {
                return;
            }

            boolean paused = (boolean) dataSnapshot.getValue();
            if (paused) {
                onToggleListener.onPauseGame();
            } else {
                onToggleListener.onResumeGame();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // TODO what
        }
    };

    void startObserving() {
        DatabaseReference pausedReference = database.getReference("game/paused");
        pausedReference.addValueEventListener(toggleListener);
    }

    void stopObserving() {
        DatabaseReference pausedReference = database.getReference("game/paused");
        pausedReference.removeEventListener(toggleListener);
    }

    interface OnToggleListener {
        void onPauseGame();

        void onResumeGame();
    }

}
