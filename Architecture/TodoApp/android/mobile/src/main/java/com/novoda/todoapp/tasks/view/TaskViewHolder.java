package com.novoda.todoapp.tasks.view;

import android.support.v7.widget.RecyclerView;

import com.novoda.data.SyncedData;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.tasks.displayer.TasksActionListener;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    public TaskViewHolder(TaskItemView itemView) {
        super(itemView);
    }

    public void bind(SyncedData<Task> syncedData, TasksActionListener tasksActionListener) {
        itemView().display(syncedData, tasksActionListener);
    }

    private TaskItemView itemView() {
        return (TaskItemView) itemView;
    }


}
