package com.novoda.frankboylan.meetingseating.settings;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class SettingsActivity extends AppCompatActivity implements SettingsDisplayer {
    DrawerLayout drawerLayout;
    SettingsPresenterImpl settingsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        ListView drawerList = findViewById(R.id.side_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);

        final TextView tvLoggedUser = findViewById(R.id.tv_drawer_greeting);

        DatabaseReference firebaseDb = FirebaseDatabase.getInstance().getReference();
        firebaseDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (auth.getUid() == null) {
                    throw new IllegalStateException("Never expected UserID to be null. Fix quick.");
                }
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

        Toolbar toolbarSettings = findViewById(R.id.toolbar_settings);
        toolbarSettings.setTitle(getString(R.string.toolbar_settings_title));

        toolbarSettings.setNavigationIcon(R.drawable.ic_action_burger);

        setSupportActionBar(toolbarSettings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsPresenter = new SettingsPresenterImpl(this, SettingsModelFactory.build(this));
        settingsPresenter.bind(this);
    }

    @Override
    protected void onDestroy() {
        settingsPresenter.unbind();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_toolbar_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                break;
        }
        return true;
    }

    public void handlerLoadDatasetSml(View v) {
        settingsPresenter.loadDataset(0);
    }

    public void handlerLoadDatasetMed(View v) {
        settingsPresenter.loadDataset(1);
    }

    public void handlerLoadDatasetLrg(View v) {
        settingsPresenter.loadDataset(2);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
