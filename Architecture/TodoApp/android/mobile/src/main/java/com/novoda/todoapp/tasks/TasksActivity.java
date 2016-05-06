package com.novoda.todoapp.tasks;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.novoda.notils.caster.Views;
import com.novoda.todoapp.BaseActivity;
import com.novoda.todoapp.R;
import com.novoda.todoapp.TodoApplication;
import com.novoda.todoapp.tasks.view.loading.AndroidTasksLoadingDisplayer;
import com.novoda.todoapp.navigation.AndroidNavigator;
import com.novoda.todoapp.tasks.displayer.TasksActionListener;
import com.novoda.todoapp.tasks.presenter.TasksPresenter;
import com.novoda.todoapp.tasks.view.TasksView;

public class TasksActivity extends BaseActivity {

    private static final String KEY_FILTER = "KEY_FILTER";

    private TasksView tasksView;
    private TasksPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_activity);
        tasksView = Views.findById(this, R.id.content);
        presenter = new TasksPresenter(
                TodoApplication.TASKS_SERVICE,
                tasksView,
                new AndroidTasksLoadingDisplayer(tasksView.getLoadingView(), tasksView.getContentView()),
                new AndroidNavigator(this)
        );
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER)) {
            presenter.setInitialFilterTo(TasksActionListener.Filter.valueOf(savedInstanceState.getString(KEY_FILTER)));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startPresenting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(tasksView.getMenuResId(), menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return tasksView.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopPresenting();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_FILTER, presenter.getCurrentFilter().name());
        super.onSaveInstanceState(outState);
    }
}
