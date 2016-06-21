package com.novoda.bonfire.login.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.novoda.bonfire.R;
import com.novoda.bonfire.login.displayer.LoginDisplayer;
import com.novoda.bonfire.view.BubblyDrawable;
import com.novoda.notils.caster.Views;

public class LoginView extends LinearLayout implements LoginDisplayer {

    private View loginButton;

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_login_view, this);
        loginButton = Views.findById(this, R.id.sign_in_button);
        View iconFrame = Views.findById(this, R.id.login_icon_frame_layout);
        iconFrame.setBackground(new BubblyDrawable(getResources()));
    }

    @Override
    public void attach(final LoginActionListener actionListener) {
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionListener.onGooglePlusLoginSelected();
            }
        });
    }

    @Override
    public void detach(LoginActionListener actionListener) {
        loginButton.setOnClickListener(null);
    }

    @Override
    public void showAuthenticationError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show(); //TODO improve error display
    }

}
