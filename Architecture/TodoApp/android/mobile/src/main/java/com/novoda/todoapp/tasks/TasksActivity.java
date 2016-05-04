package com.novoda.todoapp.tasks;

import android.os.Bundle;

import com.novoda.notils.caster.Views;
import com.novoda.todoapp.BaseActivity;
import com.novoda.todoapp.R;
import com.novoda.todoapp.TodoApplication;
import com.novoda.todoapp.loading.AndroidLoadingDisplayer;
import com.novoda.todoapp.navigation.AndroidNavigator;
import com.novoda.todoapp.tasks.presenter.TasksPresenter;
import com.novoda.todoapp.tasks.view.TasksView;

public class TasksActivity extends BaseActivity {

    private TasksPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_activity);
        TasksView tasksView = Views.findById(this, R.id.content);
        presenter = new TasksPresenter(
                TodoApplication.TASKS_SERVICE,
                tasksView,
                new AndroidLoadingDisplayer(tasksView.getLoadingView(), tasksView),
                new AndroidNavigator(this)
        );
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

}
