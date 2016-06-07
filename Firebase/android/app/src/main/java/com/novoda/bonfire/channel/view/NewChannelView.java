package com.novoda.bonfire.channel.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.novoda.bonfire.R;
import com.novoda.bonfire.channel.displayer.NewChannelDisplayer;
import com.novoda.notils.caster.Views;

public class NewChannelView extends LinearLayout implements NewChannelDisplayer {

    private ChannelCreationListener channelCreationListener;
    private EditText newChannelName;
    private Switch privateChannelSwitch;
    private Button createButton;

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
        createButton = Views.findById(this, R.id.createButton);
    }

    @Override
    public void attach(final ChannelCreationListener channelCreationListener) {
        this.channelCreationListener = channelCreationListener;
        newChannelName.addTextChangedListener(channelNameTextWatcher);
        createButton.setOnClickListener(createButtonClickListener);
        createButton.setEnabled(false);
    }

    @Override
    public void detach(ChannelCreationListener channelCreationListener) {
        newChannelName.removeTextChangedListener(channelNameTextWatcher);
        privateChannelSwitch.setOnCheckedChangeListener(null);
        createButton.setOnClickListener(null);
        this.channelCreationListener = null;
    }

    @Override
    public void showChannelCreationError() {
        setChannelNameError(R.string.channel_cannot_be_created);
    }

    private void setChannelNameError(int stringId) {
        newChannelName.setError(getContext().getString(stringId));
    }

    private final TextWatcher channelNameTextWatcher = new TextWatcher() {

        private boolean isValidInput;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            isValidInput = inputWasEmpty(start, before) && sequenceIsValidEmoji(s, start, count)
                    || characterRemoved(count) && sequenceIsValidEmoji(s, 0, start);
        }

        private boolean inputWasEmpty(int start, int before) {
            return start == 0 && before == 0;
        }

        private boolean characterRemoved(int count) {
            return count == 0;
        }

        private boolean sequenceIsValidEmoji(CharSequence sequence, int start, int count) {
            boolean isSequenceValid = true;
            for (int i = start; i < (start + count); i++) {
                char character = sequence.charAt(i);
                isSequenceValid = isSequenceValid && isEmojiComponent(character);
            }
            return isSequenceValid;
        }

        private boolean isEmojiComponent(char c) {
            int type = Character.getType(c);
            return type == Character.NON_SPACING_MARK || type == Character.SURROGATE || type == Character.OTHER_SYMBOL;
        }

        @Override
        public void afterTextChanged(Editable s) {
            createButton.setEnabled(s.length() > 0 && isValidInput);
            if (!isValidInput) {
                setChannelNameError(R.string.only_single_emoji_allowed);
            }
        }
    };

    private final OnClickListener createButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            channelCreationListener.onCreateChannelClicked(newChannelName.getText().toString(), privateChannelSwitch.isChecked());
        }
    };
}
