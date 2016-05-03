package com.novoda.todoapp.navigation;

import android.app.Activity;
import android.content.Intent;

import com.novoda.todoapp.task.TaskDetailActivity;
import com.novoda.todoapp.task.data.model.Task;

public class AndroidNavigator implements Navigator {

    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID"; //TODO handle this better

    private final Activity activity;

    public AndroidNavigator(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void toTaskDetail(Task task) {
        Intent intent = new Intent(activity, TaskDetailActivity.class);
        intent.putExtra(EXTRA_TASK_ID, task.id().value());
        activity.startActivity(intent);
    }

}
