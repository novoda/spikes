package com.blundell.mra;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView blueprint = findViewById(R.id.imageView);
        blueprint.setImageResource(R.drawable.liverpool_downstairs);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("offices");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot office : dataSnapshot.getChildren()) {
                    FirebaseOfficeHeatMaps value = office.getValue(FirebaseOfficeHeatMaps.class);
                    Log.d("TUT", "Value is: " + value);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TUT", "Failed to read value.", error.toException());
            }
        });
        AbsoluteLayout parent = findViewById(R.id.absolute_hacks);

        ImageView view1 = createSeatHeat(2, 5, 90);
        ImageView view2 = createSeatHeat(1, 5, 20);
        parent.addView(view1);
        parent.addView(view2);
    }

    @NonNull
    private ImageView createSeatHeat(int gridX, int gridY, int heatPercent) {
        ImageView view = new ImageView(this);
        float density = getResources().getDisplayMetrics().density;
        int width = (int) (94 * density);
        int height = (int) (94 * density);
        int x = (int) (82 * density) * gridX;
        int y = (int) (82 * density) * gridY;
        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(width,
                                                                             height,
                                                                             x, y
        );
        view.setLayoutParams(params);
        view.setBackgroundColor(Color.argb(33, 255 * heatPercent, 0, 0));
        return view;
    }

}
