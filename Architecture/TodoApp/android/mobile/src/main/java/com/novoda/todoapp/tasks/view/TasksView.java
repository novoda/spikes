package com.novoda.todoapp.tasks.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.novoda.todoapp.tasks.data.model.Tasks;
import com.novoda.todoapp.tasks.displayer.TasksActionListener;
import com.novoda.todoapp.tasks.displayer.TasksDisplayer;

public class TasksView extends RecyclerView implements TasksDisplayer {

    private TasksAdapter adapter;

    public TasksView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        adapter = new TasksAdapter(LayoutInflater.from(getContext()));
        setLayoutManager(new LinearLayoutManager(getContext()));
        setAdapter(adapter);
    }

    @Override
    public void attach(TasksActionListener tasksActionListener) {
        adapter.setActionListener(tasksActionListener);
    }

    @Override
    public void detach(TasksActionListener tasksActionListener) {
        adapter.setActionListener(null);
    }

    @Override
    public void display(Tasks tasks) {
        adapter.update(tasks);
    }

}
