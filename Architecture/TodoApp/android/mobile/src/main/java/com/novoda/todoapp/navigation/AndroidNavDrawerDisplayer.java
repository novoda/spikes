package com.novoda.todoapp.navigation;

import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import com.novoda.todoapp.R;
import com.novoda.todoapp.tasks.view.TasksView;

public class AndroidNavDrawerDisplayer implements NavDrawerDisplayer {

    private final TasksView tasksView;

    public AndroidNavDrawerDisplayer(TasksView tasksView) {
        this.tasksView = tasksView;
    }

    @Override
    public void attach(final NavDrawerActionListener navDrawerActionListener) {
        tasksView.getNavDrawer().setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
        tasksView.closeDrawers();
    }
}
