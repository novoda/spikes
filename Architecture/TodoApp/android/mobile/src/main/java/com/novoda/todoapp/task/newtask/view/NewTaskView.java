package com.novoda.todoapp.task.newtask.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;
import com.novoda.todoapp.task.TaskActionDisplayer;
import com.novoda.todoapp.task.TaskActionListener;
import com.novoda.todoapp.views.TodoAppBar;

public class NewTaskView extends CoordinatorLayout implements TaskActionDisplayer {
    private TextView titleView;
    private TextView descriptionView;
    private FloatingActionButton saveActionButton;
    private TodoAppBar todoAppBar;

    public NewTaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_task_edit_view, this);
        titleView = Views.findById(this, R.id.task_title);
        descriptionView = Views.findById(this, R.id.task_description);
        saveActionButton = Views.findById(this, R.id.fab_task_done);
        todoAppBar = Views.findById(this, R.id.app_bar);
        Toolbar toolbar = todoAppBar.getToolbar();
        toolbar.setTitle(R.string.new_to_do);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationContentDescription(R.string.navigate_up);
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
    public void attach(final TaskActionListener taskActionListener) {
        saveActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskActionListener.save(getTitle(), getDescription());
            }
        });
        todoAppBar.getToolbar().setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                taskActionListener.onUpSelected();
            }
        });
    }

    @Override
    public void detach(TaskActionListener taskActionListener) {
        saveActionButton.setOnClickListener(null);
        todoAppBar.getToolbar().setNavigationOnClickListener(null);
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar.make(this, R.string.empty_task_message, Snackbar.LENGTH_LONG).show(); //TODO maybe this should be extracted
    }
}
