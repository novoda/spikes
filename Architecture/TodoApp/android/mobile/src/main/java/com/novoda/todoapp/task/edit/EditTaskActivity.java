package com.novoda.todoapp.task.edit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.novoda.todoapp.R;
import com.novoda.todoapp.TodoApplication;
import com.novoda.todoapp.navigation.AndroidNavigator;
import com.novoda.todoapp.task.data.model.Id;
import com.novoda.todoapp.task.edit.displayer.EditTaskDisplayer;
import com.novoda.todoapp.task.edit.presenter.EditTaskPresenter;

public class EditTaskActivity extends AppCompatActivity {

    private EditTaskPresenter taskPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task_activity);
        Id taskId = getTaskIdFromExtras();
        taskPresenter = new EditTaskPresenter(
                taskId,
                TodoApplication.TASKS_SERVICE,
                ((EditTaskDisplayer) findViewById(R.id.content)),
                new AndroidNavigator(this)
        );
    }

    private Id getTaskIdFromExtras() {
        if (getIntent().hasExtra(AndroidNavigator.EXTRA_TASK_ID)) {
            return Id.from(getIntent().getStringExtra(AndroidNavigator.EXTRA_TASK_ID));
        } else {
            throw new IllegalStateException("Must supply task ID in Intent");
        }
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
