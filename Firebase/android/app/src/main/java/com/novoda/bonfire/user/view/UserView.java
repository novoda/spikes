package com.novoda.bonfire.user.view;

import android.content.Context;
import android.util.TypedValue;
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
        setSelectableBackgroundFromAttributes(context);
    }

    private void configureViewParams() {
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.list_item_horizontal_margin);
        int verticalMargin = getResources().getDimensionPixelSize(R.dimen.list_item_vertical_margin);
        setPadding(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
    }

    private void setSelectableBackgroundFromAttributes(Context context) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        setBackgroundResource(outValue.resourceId);
    }

    public void display(UsersView.SelectableUser user) {
        name.setText(user.user.getName());
        setSelected(user.isSelected);
    }
}
