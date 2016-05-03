package com.novoda.todoapp.loading;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;
import com.novoda.todoapp.loading.displayer.LoadingDisplayer;
import com.novoda.todoapp.loading.displayer.RetryActionListener;

public class LoadingView extends FrameLayout implements LoadingDisplayer {

    private View contentView;
    private View loadingContainer;
    private TextView loadingLabel;

    private Snackbar snackBar;

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = Views.findById(this, R.id.content);
        loadingContainer = Views.findById(this, R.id.loadingContainer);
        loadingLabel = Views.findById(this, R.id.loadingLabel);
    }

    @Override
    public void attach(final RetryActionListener retryActionListener) {
        Views.findById(this, R.id.loadingButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                retryActionListener.onRetry();
            }
        });
    }

    @Override
    public void detach(RetryActionListener retryActionListener) {
        Views.findById(this, R.id.loadingButton).setOnClickListener(null);
    }

    @Override
    public void showLoadingIndicator() {
        contentView.setVisibility(VISIBLE);
        loadingContainer.setVisibility(GONE);
        displaySnackBar("Still loading data");
    }

    @Override
    public void showLoadingScreen() {
        contentView.setVisibility(GONE);
        loadingContainer.setVisibility(VISIBLE);
        loadingLabel.setText("LOADING");
    }

    @Override
    public void showData() {
        dismissSnackBar();
        contentView.setVisibility(VISIBLE);
        loadingContainer.setVisibility(GONE);
    }

    @Override
    public void showEmptyScreen() {
        dismissSnackBar();
        contentView.setVisibility(GONE);
        loadingContainer.setVisibility(VISIBLE);
        loadingLabel.setText("EMPTY");
    }

    @Override
    public void showErrorIndicator() {
        contentView.setVisibility(VISIBLE);
        loadingContainer.setVisibility(GONE);
        displaySnackBar("An error has occurred");
    }

    @Override
    public void showErrorScreen() {
        dismissSnackBar();
        contentView.setVisibility(GONE);
        loadingContainer.setVisibility(VISIBLE);
        loadingLabel.setText("ERROR");
    }

    private void dismissSnackBar() {
        if (snackBar != null) {
            snackBar.dismiss();
        }
    }

    private void displaySnackBar(String message) {
        if (snackBar != null && snackBar.isShown()) {
            snackBar.setText(message);
        } else {
            snackBar = Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE);
            snackBar.show();
        }
    }

}
