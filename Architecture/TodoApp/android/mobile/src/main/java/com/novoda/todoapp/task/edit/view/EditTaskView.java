package com.novoda.todoapp.task.edit.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.novoda.data.SyncedData;
import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.task.edit.displayer.EditTaskDisplayer;
import com.novoda.todoapp.task.newtask.displayer.TaskActionListener;
import com.novoda.todoapp.views.TodoAppBar;

public class EditTaskView extends CoordinatorLayout implements EditTaskDisplayer {

    private TextView titleView;
    private TextView descriptionView;
    private FloatingActionButton editActionButton;

    public EditTaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_task_edit_view, this);
        titleView = Views.findById(this, R.id.task_title);
        descriptionView = Views.findById(this, R.id.task_description);
        editActionButton = Views.findById(this, R.id.fab_task_done);
        TodoAppBar todoAppBar = Views.findById(this, R.id.app_bar);
        todoAppBar.getToolbar().setTitle(R.string.edit_to_do);
    }

    @Override
    public void attach(final TaskActionListener taskActionListener) {
        editActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                taskActionListener.save(getTitle(), getDescription());
            }
        });
    }

    @Override
    public void detach(TaskActionListener taskActionListener) {
        editActionButton.setOnClickListener(null);
    }

    @Override
    public void display(SyncedData<Task> syncedData) {
        final Task task = syncedData.data();
        titleView.setText(task.title().orNull());
        descriptionView.setText(task.description().orNull());
    }

    private Optional<String> getTitle() {
        return getText(titleView);
    }

    private Optional<String> getDescription() {
        return getText(descriptionView);
    }

    private Optional<String> getText(TextView editText) {
        if (titleView.getText().toString().isEmpty()) {
            return Optional.absent();
        } else {
            return Optional.of(editText.getText().toString());
        }
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar.make(this, R.string.empty_task_message, Snackbar.LENGTH_LONG).show(); //TODO maybe this should be extracted
    }
}
