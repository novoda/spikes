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
import com.novoda.bonfire.user.data.model.Users;
import com.novoda.bonfire.user.displayer.UsersDisplayer;
import com.novoda.notils.caster.Views;

public class UsersView extends LinearLayout implements UsersDisplayer {

    private UsersAdapter usersAdapter;
    private RecyclerView recyclerView;
    private Button completeButton;

    public UsersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_users_view, this);
        usersAdapter = new UsersAdapter();
        recyclerView = Views.findById(this, R.id.usersRecyclerView);
        completeButton = Views.findById(this, R.id.completeAddingUsersButton);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(usersAdapter);
    }

    @Override
    public void attach(final SelectionListener selectionListener) {
        usersAdapter.attach(selectionListener);
        completeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionListener.onCompleteClicked();
            }
        });
    }

    @Override
    public void detach(SelectionListener selectionListener) {
        usersAdapter.detach(selectionListener);
        completeButton.setOnClickListener(null);
    }

    @Override
    public void display(Users users) {
        usersAdapter.update(users);
    }

    @Override
    public void showFailure() {
        //TODO no toast
        Toast.makeText(getContext(), "Cannot add users to the channel", Toast.LENGTH_LONG).show();
    }
}
