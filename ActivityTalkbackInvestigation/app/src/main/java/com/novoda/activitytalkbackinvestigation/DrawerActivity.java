package com.novoda.activitytalkbackinvestigation;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public abstract class DrawerActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.drawer_layout);

        ViewGroup contentView = (ViewGroup) findViewById(R.id.content_frame);

        getLayoutInflater().inflate(layoutResID, contentView);
        setupDrawer();
    }

    private void setupDrawer() {
        String[] planetTitles = new String[]{"Earth", "Saturn"};
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView listView = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        listView.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        planetTitles
                )
        );

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startActivity(createIntentForItem(position));
                    }

                    private Intent createIntentForItem(int position) {
                        if (position == 0) {
                            return new Intent(DrawerActivity.this, EarthActivity.class);
                        } else {
                            return new Intent(DrawerActivity.this, SaturnActivity.class);
                        }
                    }
                }
        );

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
    }
}
