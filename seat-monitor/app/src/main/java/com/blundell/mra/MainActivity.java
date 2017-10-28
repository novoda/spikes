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
        final AbsoluteLayout parent = findViewById(R.id.absolute_hacks);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("offices");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot offices : dataSnapshot.getChildren()) {
                    FirebaseOfficeHeatMaps office = offices.getValue(FirebaseOfficeHeatMaps.class);
                    for (FirebaseRoom room : office.getRooms()) {
                        for (FirebaseSeatHeat seatHeat : room.getSeatHeats()) {
                            SeatGrid.Position position = SeatGrid.lookup(seatHeat.getSeatId());
                            ImageView overlay = createSeatHeat(position.getX(), position.getY(), seatHeat.getHeat());
                            parent.addView(overlay);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TUT", "Failed to read value.", error.toException());
            }
        });
    }

    @NonNull
    private ImageView createSeatHeat(int gridX, int gridY, int heatPercent) {
        ImageView view = new ImageView(this);
        float density = getResources().getDisplayMetrics().density;
        int width = (int) (106 * density);
        int height = (int) (94 * density);
        int x = (int) (96 * density) * gridX;
        int y = (int) (80 * density) * gridY;
        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(width,
                                                                             height,
                                                                             x, y
        );
        view.setLayoutParams(params);

        int color = ColorScale.getColor(heatPercent);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        view.setBackgroundColor(Color.argb(44, red, green, blue));
        return view;
    }

}
