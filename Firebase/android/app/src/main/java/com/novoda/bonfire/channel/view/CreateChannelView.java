package com.novoda.bonfire.channel.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.novoda.bonfire.R;
import com.novoda.bonfire.channel.displayer.CreateChannelDisplayer;
import com.novoda.notils.caster.Views;

public class CreateChannelView extends LinearLayout implements CreateChannelDisplayer {

    private InteractionListener interactionListener = InteractionListener.NO_OP;
    private EditText newChannelName;
    private Switch privateChannelSwitch;
    private EditText addChannelMember;
    private Button createButton;

    public CreateChannelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_create_channel_view, this);
        newChannelName = Views.findById(this, R.id.newChannelName);
        privateChannelSwitch = Views.findById(this, R.id.privateChannel);
        addChannelMember = Views.findById(this, R.id.addChannelMember);
        createButton = Views.findById(this, R.id.createButton);
    }

    @Override
    public void attach(InteractionListener interactionListener) {
        this.interactionListener = interactionListener;
    }

    @Override
    public void detach(InteractionListener interactionListener) {
        this.interactionListener = InteractionListener.NO_OP;
    }

    @Override
    public void enableChannelCreation() {
        createButton.setEnabled(true);
    }

    @Override
    public void disableChannelCreation() {
        createButton.setEnabled(false);
    }

    @Override
    public void enableAddingMembers() {
        addChannelMember.setVisibility(VISIBLE);
        addChannelMember.setEnabled(true);
    }

    @Override
    public void disableAddingMembers() {
        addChannelMember.setVisibility(GONE);
        addChannelMember.setEnabled(false);
    }
}
