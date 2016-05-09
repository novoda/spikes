package com.novoda.firechat.login.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.firechat.R;
import com.novoda.firechat.login.displayer.LoginDisplayer;
import com.novoda.notils.caster.Views;

public class LoginView extends LinearLayout implements LoginDisplayer {

    TextView userView;
    View loginButton;

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_login_view, this);
        userView = Views.findById(this, R.id.userEdit);
        loginButton = Views.findById(this, R.id.loginButton);
    }

    @Override
    public void attach(final LoginActionListener actionListener) {
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionListener.onUserNameEntered(userView.getText().toString());
            }
        });
    }

    @Override
    public void detach(LoginActionListener actionListener) {
        loginButton.setOnClickListener(null);
    }

}
