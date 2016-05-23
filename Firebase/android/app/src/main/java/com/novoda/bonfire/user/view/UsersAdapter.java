package com.novoda.bonfire.user.view;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;
import com.novoda.bonfire.user.displayer.UsersDisplayer;

import java.util.ArrayList;

class UsersAdapter extends RecyclerView.Adapter<UserViewHolder>{

    private Users users = new Users(new ArrayList<User>());
    private UsersDisplayer.SelectionListener selectionListener = UsersDisplayer.SelectionListener.NO_OP;
    public void update(Users users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(new UserView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bind(users.getUserAt(position), userSelectionListener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void attach(UsersDisplayer.SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    public void detach(UsersDisplayer.SelectionListener selectionListener) {
        this.selectionListener = UsersDisplayer.SelectionListener.NO_OP;
    }

    private final UserViewHolder.UserSelectionListener userSelectionListener = new UserViewHolder.UserSelectionListener() {
        @Override
        public void onUserSelected(User user) {
            selectionListener.onUserSelected(user);
        }

        @Override
        public void onUserDeselected(User user) {

        }
    };
}
