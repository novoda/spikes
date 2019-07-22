package com.novoda.simonsaysandroidthings;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.novoda.simonsaysandroidthings.game.Score;

class FirebaseScore implements Score {

    private static final String KEY_HIGHSCORE = "highscore";
    private static final String KEY_CURRENT_SCORE = "current_score";
    private static final String TAG = "FirebaseHighscore";

    private final DatabaseReference databaseReference;
    private int currentHighscore;

    FirebaseScore(DatabaseReference reference) {
        databaseReference = reference;
        databaseReference.child(KEY_HIGHSCORE).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot + "]");
                currentHighscore = ((Long) dataSnapshot.getValue()).intValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled() called with: databaseError = [" + databaseError + "]");
            }
        });
    }

    @Override
    public int currentHighscore() {
        Log.d(TAG, "currentHighscore() returned: " + currentHighscore);
        return currentHighscore;
    }

    @Override
    public void setNewHighscore(int score) {
        Log.d(TAG, "setNewHighscore() called with: score = [" + score + "]");
        databaseReference.child(KEY_HIGHSCORE).setValue(score).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete() called with: task.isSuccessful() = [" + task.isSuccessful() + "]");
            }
        });
    }

    @Override
    public void setCurrentScore(int score) {
        Log.d(TAG, "setCurrentScore() called with: score = [" + score + "]");
        databaseReference.child(KEY_CURRENT_SCORE).setValue(score).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete() called with: task.isSuccessful() = [" + task.isSuccessful() + "]");
            }
        });
    }
}
