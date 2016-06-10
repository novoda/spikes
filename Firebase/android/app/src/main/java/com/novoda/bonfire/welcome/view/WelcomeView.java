package com.novoda.bonfire.welcome.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.bonfire.R;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.welcome.displayer.WelcomeDisplayer;
import com.novoda.notils.caster.Views;

public class WelcomeView extends LinearLayout implements WelcomeDisplayer {

    private TextView welcomeMessage;
    private InteractionListener interactionListener;

    public WelcomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflate(getContext(), R.layout.merge_welcome_view, this);
        welcomeMessage = Views.findById(this, R.id.welcomeMessageView);
    }

    @Override
    public void attach(InteractionListener interactionListener) {
        this.interactionListener = interactionListener;
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeView.this.interactionListener.onGetStartedClicked();
            }
        });
    }

    @Override
    public void detach(InteractionListener interactionListener) {
        this.interactionListener = null;
        setOnClickListener(null);
    }

    @Override
    public void display(User sender) {
        welcomeMessage.setText(sender.getName() + " invited you to Bonfire.\nTap to login and enjoy the emoji awesomeness.");
    }
}
