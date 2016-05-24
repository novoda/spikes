package com.novoda.bonfire.user.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.bonfire.R;
import com.novoda.notils.caster.Views;

public class UserView extends LinearLayout {

    private final TextView name;

    public UserView(Context context) {
        super(context);
        configureViewParams();
        View.inflate(context, R.layout.merge_user_item_view, this);
        name = Views.findById(this, R.id.userName);
        setBackgroundResource(R.drawable.user_item_selector);
    }

    private void configureViewParams() {
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.list_item_horizontal_margin);
        int verticalMargin = getResources().getDimensionPixelSize(R.dimen.list_item_vertical_margin);
        setPadding(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
    }

    public void display(UsersView.SelectableUser user) {
        name.setText(user.user.getName());
        setSelected(user.isSelected);
    }
}
