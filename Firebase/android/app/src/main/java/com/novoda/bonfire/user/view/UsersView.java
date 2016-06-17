package com.novoda.bonfire.user.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
    private List<SelectableUser> selectableUsers;
    private Toolbar toolbar;
    private SelectionListener selectionListener;

    public UsersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_users_view, this);
        recyclerView = Views.findById(this, R.id.users_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        toolbar = Views.findById(this, R.id.toolbar);
        toolbar.setTitle(R.string.members);
        toolbar.inflateMenu(R.menu.users_menu);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void attach(final SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
        usersAdapter = new UsersAdapter(selectionListener, LayoutInflater.from(getContext()));
        recyclerView.setAdapter(usersAdapter);

        toolbar.setOnMenuItemClickListener(menuItemClickListener);
    }

    @Override
    public void detach(SelectionListener selectionListener) {
        this.selectionListener = null;
        toolbar.setOnMenuItemClickListener(null);
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
            boolean isSelected = isUserSelected(selectedUsers, selectableUser);
            usersWithUpdatedSelection.add(new SelectableUser(selectableUser.user, isSelected));
        }
        selectableUsers = usersWithUpdatedSelection;
        usersAdapter.update(selectableUsers);
        toolbar.setTitle(selectedUsers.size() + " Selected");
    }

    private boolean isUserSelected(Users selectedUsers, SelectableUser selectableUser) {
        boolean foundMatch = false;
        for (User selectedUser : selectedUsers.getUsers()) {
            if (selectedUser.equals(selectableUser.user)) {
                foundMatch = true;
                break;
            }
        }
        return foundMatch;
    }

    private Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.usersDoneButton) {
                selectionListener.onCompleteClicked();
            }
            return false;
        }
    };

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
