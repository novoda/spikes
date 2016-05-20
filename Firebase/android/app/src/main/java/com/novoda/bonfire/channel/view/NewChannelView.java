package com.novoda.bonfire.channel.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.novoda.bonfire.R;
import com.novoda.bonfire.channel.displayer.NewChannelDisplayer;
import com.novoda.notils.caster.Views;

public class NewChannelView extends LinearLayout implements NewChannelDisplayer {

    private InteractionListener interactionListener = InteractionListener.NO_OP;
    private EditText newChannelName;
    private Switch privateChannelSwitch;
    private EditText channelMemberName;
    private Button createButton;
    private View addMemberView;
    private Button addMemberButton;
    public NewChannelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_create_channel_view, this);
        newChannelName = Views.findById(this, R.id.newChannelName);
        privateChannelSwitch = Views.findById(this, R.id.privateChannel);
        channelMemberName = Views.findById(this, R.id.channelMemberName);
        createButton = Views.findById(this, R.id.createButton);
        addMemberView = findViewById(R.id.addMemberView);
        addMemberButton = Views.findById(this, R.id.addChannelMemberButton);
    }

    @Override
    public void attach(final InteractionListener interactionListener) {
        this.interactionListener = interactionListener;
        newChannelName.addTextChangedListener(channelNameTextWatcher);
        privateChannelSwitch.setOnCheckedChangeListener(privateSwitchCheckedListener);
        createButton.setOnClickListener(createButtonClickListener);
        addMemberButton.setOnClickListener(addMemberClickListener);
    }

    @Override
    public void detach(InteractionListener interactionListener) {
        this.interactionListener = InteractionListener.NO_OP;
        newChannelName.removeTextChangedListener(channelNameTextWatcher);
        privateChannelSwitch.setOnCheckedChangeListener(null);
        createButton.setOnClickListener(null);
        addMemberButton.setOnClickListener(null);
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
        addMemberView.setVisibility(VISIBLE);
        channelMemberName.setEnabled(true);
    }

    @Override
    public void disableAddingMembers() {
        addMemberView.setVisibility(GONE);
        channelMemberName.setEnabled(false);
    }

    @Override
    public void showChannelCreationError() {
        newChannelName.setError(getContext().getString(R.string.channel_already_exists));
    }

    private final TextWatcher channelNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            interactionListener.onChannelNameLengthChanged(s.length());
        }
    };

    private final CompoundButton.OnCheckedChangeListener privateSwitchCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            interactionListener.onPrivateChannelSwitchStateChanged(isChecked);
        }
    };

    private final OnClickListener addMemberClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            interactionListener.onAddOwner(channelMemberName.getText().toString());
        }
    };

    private final OnClickListener createButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            interactionListener.onCreateChannelClicked(newChannelName.getText().toString(), privateChannelSwitch.isChecked());
        }
    };
}
