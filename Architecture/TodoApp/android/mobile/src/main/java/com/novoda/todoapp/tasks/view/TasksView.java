package com.novoda.todoapp.tasks.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;
import com.novoda.todoapp.loading.LoadingView;
import com.novoda.todoapp.tasks.data.model.Tasks;
import com.novoda.todoapp.tasks.displayer.TasksActionListener;
import com.novoda.todoapp.tasks.displayer.TasksDisplayer;

public class TasksView extends LinearLayout implements TasksDisplayer {

    private TasksAdapter adapter;

    public TasksView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_tasks_view, this);
        RecyclerView recyclerView = Views.findById(this, R.id.tasks_list);
        adapter = new TasksAdapter(LayoutInflater.from(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
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

    public LoadingView getLoadingView() {
        return Views.findById(this, R.id.loading_view);
    }

}
