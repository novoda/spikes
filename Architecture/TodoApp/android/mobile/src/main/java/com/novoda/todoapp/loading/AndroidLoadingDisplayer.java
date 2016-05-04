package com.novoda.todoapp.loading;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.novoda.todoapp.loading.displayer.LoadingDisplayer;
import com.novoda.todoapp.loading.displayer.RetryActionListener;

public class AndroidLoadingDisplayer implements LoadingDisplayer {

    private final LoadingView loadingView;
    private final View contentView;

    private Snackbar snackBar;

    public AndroidLoadingDisplayer(LoadingView loadingView, View contentView) {
        this.loadingView = loadingView;
        this.contentView = contentView;
    }

    @Override
    public void attach(final RetryActionListener retryActionListener) {
        loadingView.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryActionListener.onRetry();
            }
        });
    }

    @Override
    public void detach(RetryActionListener retryActionListener) {
        loadingView.setRetryButtonClickListener(null);
    }

    @Override
    public void showLoadingIndicator() {
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        displaySnackBar("Still loading data");
    }

    @Override
    public void showLoadingScreen() {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        loadingView.setAsLoading();
    }

    @Override
    public void showData() {
        dismissSnackBar();
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyScreen() {
        dismissSnackBar();
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        loadingView.setAsEmpty();
    }

    @Override
    public void showErrorIndicator() {
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        displaySnackBar("An error has occurred");
    }

    @Override
    public void showErrorScreen() {
        dismissSnackBar();
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        loadingView.setAsError();
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
            snackBar = Snackbar.make(loadingView, message, Snackbar.LENGTH_INDEFINITE);
            snackBar.show();
        }
    }
}
