package com.novoda.bonfire.user.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.novoda.bonfire.R;
import com.novoda.bonfire.view.CircleCropImageTransformation;
import com.novoda.notils.caster.Views;

public class UserView extends FrameLayout {

    private final TextView name;
    private final ImageView image;
    private final CircleCropImageTransformation circleCropTransformation;

    public UserView(Context context) {
        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View.inflate(context, R.layout.merge_user_item_view, this);
        name = Views.findById(this, R.id.userName);
        image = Views.findById(this, R.id.userImage);
        setBackgroundResource(R.drawable.user_item_selector);
        circleCropTransformation = new CircleCropImageTransformation(context);
    }

    public void display(UsersView.SelectableUser user) {
        Glide.with(getContext())
                .load(user.user.getPhotoUrl())
                .error(R.drawable.ic_person)
                .transform(circleCropTransformation)
                .into(image);
        name.setText(user.user.getName());
        setSelected(user.isSelected);
    }
}
