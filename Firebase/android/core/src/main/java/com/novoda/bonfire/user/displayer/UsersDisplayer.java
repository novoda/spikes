package com.novoda.bonfire.user.displayer;

import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;

public interface UsersDisplayer {

    void attach(SelectionListener selectionListener);

    void detach(SelectionListener selectionListener);

    void display(Users users);

    void showFailure();

    void displaySelectedUsers(Users selectedUsers);

    interface SelectionListener {

        void onUserSelected(User user);

        void onUserDeselected(User user);

        void onCompleteClicked();
    }
}
