package com.novoda.todoapp.navigation;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.novoda.todoapp.R;

public class AndroidNavDrawerDisplayer implements NavDrawerDisplayer {

    private final DrawerLayout drawerLayout;
    private final NavigationView navigationView;

    public AndroidNavDrawerDisplayer(DrawerLayout drawerLayout, NavigationView navigationView) {
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;
    }

    @Override
    public void attach(final NavDrawerActionListener navDrawerActionListener) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.to_do_list_nav_drawer_item:
                        navDrawerActionListener.onToDoListNavDrawerItemSelected();
                        return true;
                    case R.id.statistics_nav_drawer_item:
                        navDrawerActionListener.onStatisticsNavDrawerItemSelected();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public void closeNavDrawer() {
        drawerLayout.closeDrawers();
    }

    @Override
    public void detach() {
        navigationView.setNavigationItemSelectedListener(null);
    }
}
