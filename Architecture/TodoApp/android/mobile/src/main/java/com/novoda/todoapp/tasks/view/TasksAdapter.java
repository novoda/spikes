package com.novoda.todoapp.tasks.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.novoda.todoapp.R;
import com.novoda.todoapp.tasks.data.model.Tasks;
import com.novoda.todoapp.tasks.displayer.TasksActionListener;

public class TasksAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    private final LayoutInflater layoutInflater;

    private Tasks tasks = Tasks.empty();
    private TasksActionListener tasksActionListener;

    public TasksAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
        setHasStableIds(true);
    }

    public void setActionListener(TasksActionListener tasksActionListener) {
        this.tasksActionListener = tasksActionListener;
    }

    public void update(Tasks tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TaskViewHolder((TaskItemView) layoutInflater.inflate(R.layout.task_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        holder.bind(tasks.all().get(position), tasksActionListener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

}
