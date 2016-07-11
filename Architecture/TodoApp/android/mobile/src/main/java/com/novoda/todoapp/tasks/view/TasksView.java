package com.novoda.todoapp.tasks.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;
import com.novoda.todoapp.tasks.data.model.Tasks;
import com.novoda.todoapp.tasks.displayer.TasksActionListener;
import com.novoda.todoapp.tasks.displayer.TasksDisplayer;
import com.novoda.todoapp.tasks.view.loading.TasksLoadingView;
import com.novoda.todoapp.views.TodoAppBar;

public class TasksView extends DrawerLayout implements TasksDisplayer {

    private NavigationView navDrawer;
    private RecyclerView recyclerView;
    private TasksAdapter adapter;
    private TasksActionListener tasksActionListener;

    public TasksView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_tasks_view, this);
        recyclerView = Views.findById(this, R.id.tasks_list);
        adapter = new TasksAdapter(LayoutInflater.from(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        TodoAppBar todoAppBar = Views.findById(this, R.id.app_bar);
        Toolbar toolbar = todoAppBar.getToolbar();
        toolbar.inflateMenu(R.menu.tasks_menu);
        toolbar.setOnMenuItemClickListener(new TasksMenuItemClickListener());
        toolbar.setTitle(R.string.to_do_novoda);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle((Activity) getContext(), this, toolbar, R.string.app_name, R.string.app_name);
        addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navDrawer = Views.findById(this, R.id.nav_drawer);
    }

    @Override
    public void attach(final TasksActionListener tasksActionListener) {
        this.tasksActionListener = tasksActionListener;
        adapter.setActionListener(tasksActionListener);
        Views.findById(this, R.id.fab_add_task).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksActionListener.onAddTaskSelected();
            }
        });
    }

    @Override
    public void detach(TasksActionListener tasksActionListener) {
        this.tasksActionListener = null;
        adapter.setActionListener(null);
        Views.findById(this, R.id.fab_add_task).setOnClickListener(null);
    }

    @Override
    public void display(Tasks tasks) {
        adapter.update(tasks);
    }

    public TasksLoadingView getLoadingView() {
        return Views.findById(this, R.id.loading_view);
    }

    public View getContentView() {
        return recyclerView;
    }

    public NavigationView getNavDrawer() {
        return navDrawer;
    }

    private void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), Views.findById(this, R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.active:
                        tasksActionListener.onFilterSelected(TasksActionListener.Filter.ACTIVE);
                        return true;
                    case R.id.completed:
                        tasksActionListener.onFilterSelected(TasksActionListener.Filter.COMPLETED);
                        return true;
                    default:
                        tasksActionListener.onFilterSelected(TasksActionListener.Filter.ALL);
                        return true;
                }
            }
        });
        popup.show();
    }

    private class TasksMenuItemClickListener implements Toolbar.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_refresh:
                    tasksActionListener.onRefreshSelected();
                    return true;
                case R.id.menu_filter:
                    showFilteringPopUpMenu();
                    return true;
                case R.id.menu_clear:
                    tasksActionListener.onClearCompletedSelected();
                    return true;
                case R.id.menu_stats:
                    tasksActionListener.onStatisticsSelected();
                    return true;
                default:
                    return false;
            }
        }
    }

}
