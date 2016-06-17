package com.novoda.todoapp.tasks.view.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;

public class TasksLoadingView extends LinearLayout {

    private ImageView loadingIcon;
    private TextView loadingLabel;

    public TasksLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_tasks_loading_view, this);
        loadingLabel = Views.findById(this, R.id.loadingLabel);
        loadingIcon = Views.findById(this, R.id.loadingIcon);
    }

    public void setRetryButtonClickListener(OnClickListener onClickListener) {
        findViewById(R.id.loadingIcon).setOnClickListener(onClickListener);
    }

    public void setAsLoading() {
        loadingIcon.setImageResource(R.drawable.ic_loading);
        loadingLabel.setText(R.string.loading_tasks);
    }

    public void setAsError() {
        loadingIcon.setImageResource(R.drawable.ic_error);
        loadingLabel.setText(R.string.error_tasks);
    }

    public void setAsEmptyTasks() {
        loadingIcon.setImageResource(R.drawable.ic_no_tasks_all);
        loadingLabel.setText(R.string.no_tasks_all);
    }

    public void setAsEmptyActiveTasks() {
        loadingIcon.setImageResource(R.drawable.ic_no_tasks_active);
        loadingLabel.setText(R.string.no_tasks_active);
    }

    public void setAsEmptyCompletedTasks() {
        loadingIcon.setImageResource(R.drawable.ic_no_tasks_completed);
        loadingLabel.setText(R.string.no_tasks_completed);
    }
}
