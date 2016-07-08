package com.novoda.todoapp.tasks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;
import com.novoda.todoapp.TodoApplication;
import com.novoda.todoapp.navigation.AndroidNavDrawerDisplayer;
import com.novoda.todoapp.navigation.AndroidNavigator;
import com.novoda.todoapp.tasks.displayer.TasksActionListener;
import com.novoda.todoapp.tasks.presenter.TasksPresenter;
import com.novoda.todoapp.tasks.view.TasksView;
import com.novoda.todoapp.tasks.view.loading.AndroidTasksLoadingDisplayer;

public class TasksActivity extends AppCompatActivity {

    private static final String KEY_FILTER = "KEY_FILTER";

    private TasksPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_activity);
        TasksView tasksView = Views.findById(this, R.id.content);
        presenter = new TasksPresenter(
                TodoApplication.TASKS_SERVICE,
                tasksView,
                new AndroidTasksLoadingDisplayer(tasksView.getLoadingView(), tasksView.getContentView()),
                new AndroidNavDrawerDisplayer(tasksView),
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
