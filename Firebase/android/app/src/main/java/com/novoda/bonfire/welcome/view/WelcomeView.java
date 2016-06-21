package com.novoda.bonfire.welcome.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.novoda.bonfire.R;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.view.BubblyDrawable;
import com.novoda.bonfire.view.CircleCropImageTransformation;
import com.novoda.bonfire.welcome.displayer.WelcomeDisplayer;
import com.novoda.notils.caster.Views;

public class WelcomeView extends LinearLayout implements WelcomeDisplayer {

    private TextView welcomeMessage;
    private ImageView userAvatar;
    private View proceedButton;

    public WelcomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflate(getContext(), R.layout.merge_welcome_view, this);
        welcomeMessage = Views.findById(this, R.id.welcome_message_view);
        userAvatar = Views.findById(this, R.id.user_avatar);
        proceedButton = Views.findById(this, R.id.proceed_button);
        View senderFrame = Views.findById(this, R.id.welcome_sender_layout);
        senderFrame.setBackground(new BubblyDrawable(getResources()));
    }

    @Override
    public void attach(final InteractionListener interactionListener) {
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interactionListener.onGetStartedClicked();
            }
        });
    }

    @Override
    public void detach(InteractionListener interactionListener) {
        proceedButton.setOnClickListener(null);
    }

    @Override
    public void display(User sender) {
        Context context = getContext();
        Glide.with(context)
                .load(sender.getPhotoUrl())
                .error(R.drawable.ic_person)
                .transform(new CircleCropImageTransformation(context))
                .into(userAvatar);
        welcomeMessage.setText(sender.getName() + "\ninvited you to Bonfire");
    }
}
