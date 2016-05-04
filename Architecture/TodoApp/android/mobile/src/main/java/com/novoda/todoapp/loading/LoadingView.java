package com.novoda.todoapp.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;

public class LoadingView extends LinearLayout {

    private TextView loadingLabel;

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_loading_view, this);
        loadingLabel = Views.findById(this, R.id.loadingLabel);
    }

    public void setRetryButtonClickListener(OnClickListener onClickListener) {
        findViewById(R.id.loadingButton).setOnClickListener(onClickListener);
    }

    public void setAsLoading() {
        loadingLabel.setText("LOADING");
    }

    public void setAsEmpty() {
        loadingLabel.setText("EMPTY");
    }

    public void setAsError() {
        loadingLabel.setText("ERROR");
    }
}
