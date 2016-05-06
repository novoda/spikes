package com.novoda.todoapp.tasks.loading.displayer;

public interface TasksLoadingDisplayer {

    void attach(RetryActionListener retryActionListener);

    void detach(RetryActionListener retryActionListener);

    void showLoadingIndicator();

    void showLoadingScreen();

    void showData();

    void showEmptyTasksScreen();

    void showEmptyActiveTasksScreen();

    void showEmptyCompletedTasksScreen();

    void showErrorIndicator();

    void showErrorScreen();

}
