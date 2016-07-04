package com.novoda.todoapp.statistics.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.novoda.todoapp.statistics.data.model.Statistics;
import com.novoda.todoapp.statistics.displayer.StatisticsDisplayer;

public class StatisticsView extends TextView implements StatisticsDisplayer {

    public StatisticsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void display(Statistics statistics) {
        if (statistics.noTasks()) {
            setText("No Tasks");
        } else {
            setText("Active tasks: " + statistics.activeTasks() + "\nCompleted tasks: " + statistics.completedTasks());
        }
    }

}
