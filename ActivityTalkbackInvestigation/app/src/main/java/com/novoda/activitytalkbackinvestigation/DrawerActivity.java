package com.novoda.activitytalkbackinvestigation;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
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
        ListView listView = (ListView) findViewById(R.id.left_drawer);
        listView.setAdapter(createDrawerAdapter(this));
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startActivity(createIntentForItem(position, DrawerActivity.this));
                    }

                }
        );

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private static ListAdapter createDrawerAdapter(Context context) {
        return new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                new String[]{"Earth", "Saturn"}
        );
    }

    private static Intent createIntentForItem(int itemPosition, Context context) {
        if (itemPosition == 0) {
            return new Intent(context, EarthActivity.class);
        } else {
            return new Intent(context, SaturnActivity.class);
        }
    }
    
}
