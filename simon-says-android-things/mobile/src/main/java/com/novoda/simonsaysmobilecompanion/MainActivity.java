package com.novoda.simonsaysmobilecompanion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView hishcore = (TextView) findViewById(R.id.highscore);
        final TextView currentRound = (TextView) findViewById(R.id.current_round);

        valueEventListener = FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hishcore.setText(dataSnapshot.child("highscore").getValue(Long.class).toString());
                currentRound.setText(dataSnapshot.child("current_score").getValue(Long.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // nothing to do
            }
        });
    }

    @Override
    protected void onDestroy() {
        FirebaseDatabase.getInstance().getReference().removeEventListener(valueEventListener);
        super.onDestroy();
    }
}
