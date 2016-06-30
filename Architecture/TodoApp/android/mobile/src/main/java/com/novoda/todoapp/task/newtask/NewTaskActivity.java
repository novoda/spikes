package com.novoda.todoapp.task.newtask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.novoda.todoapp.R;
import com.novoda.todoapp.TodoApplication;
import com.novoda.todoapp.navigation.AndroidNavigator;
import com.novoda.todoapp.task.newtask.displayer.NewTaskDisplayer;
import com.novoda.todoapp.task.newtask.presenter.NewTaskPresenter;

public class NewTaskActivity extends AppCompatActivity {

    private NewTaskPresenter taskPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task_activity);
        taskPresenter = new NewTaskPresenter(
                TodoApplication.TASKS_SERVICE,
                ((NewTaskDisplayer) findViewById(R.id.content)),
                new AndroidNavigator(this)
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
