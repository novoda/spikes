package com.novoda.todoapp.statistics.view;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;
import com.novoda.todoapp.statistics.data.model.Statistics;
import com.novoda.todoapp.statistics.displayer.StatisticsDisplayer;
import com.novoda.todoapp.views.TodoAppBar;

public class StatisticsView extends LinearLayout implements StatisticsDisplayer {

    private TextView statisticsLabel;

    public StatisticsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_statistics_view, this);
        statisticsLabel = Views.findById(this, R.id.statistics_label);
        TodoAppBar todoAppBar = Views.findById(this, R.id.app_bar);
        Toolbar toolbar = todoAppBar.getToolbar();
        toolbar.setTitle("Statistics");
    }

    @Override
    public void display(Statistics statistics) {
        if (statistics.noTasks()) {
            statisticsLabel.setText("No Tasks");
        } else {
            statisticsLabel.setText("Active tasks: " + statistics.activeTasks() + "\nCompleted tasks: " + statistics.completedTasks());
        }
    }

}
