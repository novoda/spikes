package com.novoda.bonfire.user.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.novoda.bonfire.R;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;
import com.novoda.bonfire.user.displayer.UsersDisplayer;
import com.novoda.notils.caster.Views;

import java.util.ArrayList;
import java.util.List;

public class UsersView extends LinearLayout implements UsersDisplayer {

    private UsersAdapter usersAdapter;
    private RecyclerView recyclerView;
    private Button completeButton;
    private List<SelectableUser> selectableUsers;

    public UsersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_users_view, this);
        recyclerView = Views.findById(this, R.id.usersRecyclerView);
        completeButton = Views.findById(this, R.id.completeAddingUsersButton);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void attach(final SelectionListener selectionListener) {
        usersAdapter = new UsersAdapter(selectionListener);
        recyclerView.setAdapter(usersAdapter);
        completeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionListener.onCompleteClicked();
            }
        });
    }

    @Override
    public void detach(SelectionListener selectionListener) {
        completeButton.setOnClickListener(null);
    }

    @Override
    public void display(Users users) {
        selectableUsers = toSelectableUsers(users);
        usersAdapter.update(selectableUsers);
    }

    @Override
    public void showFailure() {
        //TODO no toast
        Toast.makeText(getContext(), "Cannot add users to the channel", Toast.LENGTH_LONG).show();
    }

    @Override
    public void displaySelectedUsers(Users selectedUsers) {
        List<SelectableUser> usersWithUpdatedSelection = new ArrayList<>(selectableUsers.size());
        for (SelectableUser selectableUser : selectableUsers) {
            boolean foundMatch = false;
            for (User selectedUser : selectedUsers.getUsers()) {
                if (selectedUser.equals(selectableUser.user)) {
                    usersWithUpdatedSelection.add(new SelectableUser(selectedUser, true));
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                usersWithUpdatedSelection.add(selectableUser);
            }
        }
        selectableUsers = usersWithUpdatedSelection;
        usersAdapter.update(selectableUsers);
    }

    class SelectableUser {
        public final User user;
        public final boolean isSelected;

        SelectableUser(User user, boolean isSelected) {
            this.user = user;
            this.isSelected = isSelected;
        }
    }

    private List<SelectableUser> toSelectableUsers(Users users) {
        List<SelectableUser> selectableUsers = new ArrayList<>(users.size());
        for (User user : users.getUsers()) {
            selectableUsers.add(new SelectableUser(user, false));
        }
        return selectableUsers;
    }
}
