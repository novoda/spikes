package com.novoda.todoapp.task;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.novoda.todoapp.R;
import com.novoda.todoapp.TodoApplication;
import com.novoda.todoapp.navigation.AndroidNavigator;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.displayer.TaskDisplayer;
import com.novoda.todoapp.task.presenter.TaskPresenter;

public class TaskDetailActivity extends AppCompatActivity {

    private TaskPresenter taskPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_detail_activity);
        Id taskId = Id.from(getIntent().getStringExtra(AndroidNavigator.EXTRA_TASK_ID));
        taskPresenter = new TaskPresenter(
                taskId,
                TodoApplication.TASKS_SERVICE,
                ((TaskDisplayer) findViewById(R.id.content))
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        taskPresenter.startPresenting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        taskPresenter.stopPresenting();
    }

}
