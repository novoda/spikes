package com.novoda.bonfire.channel.view;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
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
    private Toolbar toolbar;

    public NewChannelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_new_channel_view, this);
        newChannelName = Views.findById(this, R.id.new_channel_name);
        privateChannelSwitch = Views.findById(this, R.id.private_channel_switch);
        setupToolbar();
    }

    private void setupToolbar() {
        toolbar = Views.findById(this, R.id.toolbar);
        toolbar.setTitle(R.string.createChannel);
        toolbar.inflateMenu(R.menu.new_channel_menu);
        toolbar.setNavigationIcon(R.drawable.ic_clear_white);
    }

    @Override
    public void attach(final ChannelCreationListener channelCreationListener) {
        this.channelCreationListener = channelCreationListener;
        newChannelName.addTextChangedListener(channelNameTextWatcher);
        toolbar.setOnMenuItemClickListener(menuItemClickListener);
        toolbar.setNavigationOnClickListener(navigationOnClickListener);
        getCreateItem().setEnabled(false);
    }

    private MenuItem getCreateItem() {
        return toolbar.getMenu().findItem(R.id.actionCreate);
    }

    @Override
    public void detach(ChannelCreationListener channelCreationListener) {
        newChannelName.removeTextChangedListener(channelNameTextWatcher);
        privateChannelSwitch.setOnCheckedChangeListener(null);
        toolbar.setOnMenuItemClickListener(null);
        toolbar.setNavigationOnClickListener(null);
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
            getCreateItem().setEnabled(s.length() > 0 && isValidInput);
            if (!isValidInput) {
                setChannelNameError(R.string.only_single_emoji_allowed);
            }
        }
    };

    private final Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.actionCreate) {
                channelCreationListener.onCreateChannelClicked(newChannelName.getText().toString(), privateChannelSwitch.isChecked());
                return true;
            } else {
                return false;
            }
        }
    };

    private final OnClickListener navigationOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            channelCreationListener.onCancel();
        }
    };
}
