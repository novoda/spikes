package com.novoda.todoapp.task.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.novoda.data.SyncedData;
import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.task.displayer.TaskActionListener;
import com.novoda.todoapp.task.displayer.TaskDisplayer;

public class TaskDetailView extends CoordinatorLayout implements TaskDisplayer {

    private TextView titleView;
    private TextView descriptionView;
    private CheckBox completeCheckBox;
    private View editActionButton;

    private TaskActionListener taskActionListener;

    public TaskDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_task_detail_view, this);
        titleView = Views.findById(this, R.id.task_detail_title);
        descriptionView = Views.findById(this, R.id.task_detail_description);
        completeCheckBox = Views.findById(this, R.id.task_detail_complete);
        editActionButton = Views.findById(this, R.id.fab_edit_task);
    }

    @Override
    public void attach(TaskActionListener taskActionListener) {
        this.taskActionListener = taskActionListener;
    }

    @Override
    public void detach(TaskActionListener taskActionListener) {
        this.taskActionListener = null;
    }

    @Override
    public void display(SyncedData<Task> syncedData) {
        final Task task = syncedData.data();
        titleView.setText(task.title().orNull());
        descriptionView.setText(task.description().orNull());

        completeCheckBox.setChecked(task.isCompleted());
        completeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskActionListener.toggleCompletion(task);
            }
        });

        editActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                taskActionListener.onEditSelected(task);
            }
        });
    }

}
