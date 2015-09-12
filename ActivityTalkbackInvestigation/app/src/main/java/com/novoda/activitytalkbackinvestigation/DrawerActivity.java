package com.novoda.activitytalkbackinvestigation;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
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

    private Intent pendingNavigationClick;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.drawer_layout);
        ViewGroup contentView = (ViewGroup) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, contentView);
        setupDrawer();
    }

    private void setupDrawer() {
        ListView listView = (ListView) findViewById(R.id.left_drawer);
        listView.setAdapter(createDrawerAdapter(this));

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pendingNavigationClick = createIntentForItem(position, DrawerActivity.this);
                        drawerLayout.closeDrawers();
                    }

                }
        );

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // waiting for the drawer to close, then navigating seems to cause issue.
                // why wait for the drawer to close? jankiness.
                if (pendingNavigationClick != null) {
                    startActivity(pendingNavigationClick);
                    pendingNavigationClick = null;
                }
            }

        };
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
