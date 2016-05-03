package com.novoda.todoapp.loading.displayer;

public interface LoadingDisplayer {

    void attach(RetryActionListener retryActionListener);

    void detach(RetryActionListener retryActionListener);

    void showLoadingIndicator();

    void showLoadingScreen();

    void showData();

    void showEmptyScreen();

    void showErrorIndicator();

    void showErrorScreen();

}
