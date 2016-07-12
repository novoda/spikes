package com.novoda.todoapp.navigation;

public interface TopLevelMenuDisplayer {
    void attach(TopLevelMenuActionListener topLevelMenuActionListener);

    void closeMenu();

    void detach();
}
