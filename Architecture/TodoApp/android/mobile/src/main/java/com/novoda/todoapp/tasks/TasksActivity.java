package com.novoda.todoapp.tasks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.novoda.todoapp.R;
import com.novoda.todoapp.TodoApplication;
import com.novoda.todoapp.loading.displayer.LoadingDisplayer;
import com.novoda.todoapp.navigation.AndroidNavigator;
import com.novoda.todoapp.tasks.displayer.TasksDisplayer;
import com.novoda.todoapp.tasks.presenter.TasksPresenter;

public class TasksActivity extends AppCompatActivity {

    private TasksPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_activity);
        presenter = new TasksPresenter(
                TodoApplication.TASKS_SERVICE,
                ((TasksDisplayer) findViewById(R.id.content)),
                ((LoadingDisplayer) findViewById(R.id.loadingView)),
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
