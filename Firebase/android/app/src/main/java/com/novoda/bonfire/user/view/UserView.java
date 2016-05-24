package com.novoda.bonfire.user.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.novoda.bonfire.R;
import com.novoda.bonfire.view.CircleCropImageTransformation;
import com.novoda.notils.caster.Views;

public class UserView extends LinearLayout {

    private final TextView name;
    private final ImageView image;
    private final CircleCropImageTransformation circleCropTransformation;

    public UserView(Context context) {
        super(context);
        configureViewParams();
        View.inflate(context, R.layout.merge_user_item_view, this);
        name = Views.findById(this, R.id.userName);
        image = Views.findById(this, R.id.userImage);
        setBackgroundResource(R.drawable.user_item_selector);
        circleCropTransformation = new CircleCropImageTransformation(context);
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
        Glide.with(getContext()).load(user.user.getPhotoUrl())
                .transform(circleCropTransformation)
                .into(image);
        name.setText(user.user.getName());
        setSelected(user.isSelected);
    }
}
