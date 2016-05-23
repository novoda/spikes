package com.novoda.bonfire.user.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.novoda.bonfire.user.data.model.User;

class UserViewHolder extends RecyclerView.ViewHolder{

    private final UserView userView;

    public UserViewHolder(UserView itemView) {
        super(itemView);
        this.userView = itemView;
    }

    public void bind(final User user, final UserSelectionListener listener) {
        userView.display(user);
        userView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    v.setSelected(false);
                    listener.onUserDeselected(user);
                } else {
                    v.setSelected(true);
                    listener.onUserSelected(user);
                }
            }
        });
    }

    public interface UserSelectionListener {
        void onUserSelected(User user);

        void onUserDeselected(User user);
    }
}
