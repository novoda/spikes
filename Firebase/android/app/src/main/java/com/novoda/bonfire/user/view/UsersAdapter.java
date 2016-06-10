package com.novoda.bonfire.user.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.novoda.bonfire.R;
import com.novoda.bonfire.user.displayer.UsersDisplayer;

import java.util.ArrayList;
import java.util.List;

class UsersAdapter extends RecyclerView.Adapter<UserViewHolder>{

    private List<UsersView.SelectableUser> users = new ArrayList<>();
    private final UsersDisplayer.SelectionListener selectionListener;
    private final LayoutInflater inflater;

    UsersAdapter(UsersDisplayer.SelectionListener selectionListener, LayoutInflater inflater) {
        this.selectionListener = selectionListener;
        this.inflater = inflater;
        setHasStableIds(true);
    }

    public void update(List<UsersView.SelectableUser> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder((UserView) inflater.inflate(R.layout.user_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        final UsersView.SelectableUser user = users.get(position);
        holder.bind(user, selectionListener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).user.getId().hashCode();
    }
}
