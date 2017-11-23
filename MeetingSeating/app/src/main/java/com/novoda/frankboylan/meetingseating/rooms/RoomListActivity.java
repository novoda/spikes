package com.novoda.frankboylan.meetingseating.rooms;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novoda.frankboylan.meetingseating.DrawerItemClickListener;
import com.novoda.frankboylan.meetingseating.R;
import com.novoda.frankboylan.meetingseating.SQLiteDataManagement.SQLiteRead;

import java.util.List;

public class RoomListActivity extends AppCompatActivity implements RoomListDisplayer {
    private static final String TAG = "RoomListActivity";
    private ListView listViewRooms;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        ListView drawerList = findViewById(R.id.side_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);

        final TextView tvLoggedUser = findViewById(R.id.tv_drawer_greeting);

        DatabaseReference firebaseDb = FirebaseDatabase.getInstance().getReference();
        firebaseDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstname = dataSnapshot.child("users").child(auth.getUid()).child("firstname").getValue().toString();
                tvLoggedUser.setText("Welcome, " + firstname + "!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String[] mDrawerOptions = getResources().getStringArray(R.array.drawer_options);
        drawerList.setAdapter(new ArrayAdapter<>(this,
                                                 R.layout.drawer_list_item, mDrawerOptions
        ));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        Toolbar toolbarRoom = findViewById(R.id.toolbar_room);
        toolbarRoom.setTitle(R.string.toolbar_room_title);
        toolbarRoom.setNavigationIcon(R.drawable.ic_action_burger);
        setSupportActionBar(toolbarRoom);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewRooms = findViewById(R.id.listview_all_rooms);
        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.room_toolbar_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(this, "Fetching data", Toast.LENGTH_LONG).show();
                updateList();
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Refreshes ListViews with SQLite data
     */
    private void updateList() {
        List<Room> roomList = new SQLiteRead(this).getAllRooms();
        listViewRooms.setAdapter(new CustomRoomAdapter(this, roomList));
    }

    @Override
    public void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
