package com.novoda.activitytalkbackinvestigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class ProHaxDrawerActivity extends AppCompatActivity {

    private static final String EXTRA_INTERNAL_NAV = BuildConfig.APPLICATION_ID + "EXTRA_INTERNAL_NAV";

    private DrawerLayout drawerLayout;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.drawer_layout);
        ViewGroup contentView = (ViewGroup) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, contentView);
        setupDrawer();

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_INTERNAL_NAV)) {
            drawerLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    drawerLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
                }
            }, 1000);
        }
    }

    private void setupDrawer() {
        ListView listView = (ListView) findViewById(R.id.left_drawer);
        listView.setAdapter(createDrawerAdapter(this));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startActivity(createIntentForItem(position, ProHaxDrawerActivity.this));
                        overridePendingTransition(0, 0);
                        new Handler(getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawerLayout.closeDrawer(GravityCompat.START);
                            }
                        }, 500);
                    }

                }
        );

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
        Intent intent;
        if (itemPosition == 0) {
            intent = new Intent(context, EarthActivity.class);
        } else {
            intent = new Intent(context, SaturnActivity.class);
        }
        intent.putExtra(EXTRA_INTERNAL_NAV, true);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

}
