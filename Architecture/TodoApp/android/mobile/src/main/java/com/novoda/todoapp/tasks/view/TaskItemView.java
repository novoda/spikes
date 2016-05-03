package com.novoda.todoapp.tasks.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.data.SyncedData;
import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.tasks.displayer.TasksActionListener;

public class TaskItemView extends LinearLayout {

    private TextView titleView;
    private CheckBox completeCheckBox;

    public TaskItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_task_item_view, this);
        titleView = Views.findById(this, R.id.title);
        completeCheckBox = Views.findById(this, R.id.complete);
    }

    public void display(SyncedData<Task> syncedData, final TasksActionListener tasksActionListener) {
        final Task task = syncedData.data();
        titleView.setText(task.titleOrDescription().orNull());

        setBackground(getBackgroundFor(task));
        completeCheckBox.setChecked(task.isCompleted());
        completeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               tasksActionListener.toggleCompletion(task);
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksActionListener.onTaskSelected(task);
            }
        });
    }

    private Drawable getBackgroundFor(Task task) {
        if (task.isCompleted()) {
            return getContext()
                    .getResources().getDrawable(R.drawable.list_completed_touch_feedback);
        } else {
            return getContext()
                    .getResources().getDrawable(R.drawable.touch_feedback);
        }
    }

}
