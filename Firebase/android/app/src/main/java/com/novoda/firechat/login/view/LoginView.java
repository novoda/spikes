package com.novoda.firechat.login.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.novoda.firechat.R;
import com.novoda.firechat.login.displayer.LoginDisplayer;
import com.novoda.notils.caster.Views;

public class LoginView extends LinearLayout implements LoginDisplayer {

    private View loginButton;

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_login_view, this);
        loginButton = Views.findById(this, R.id.sign_in_button);
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
