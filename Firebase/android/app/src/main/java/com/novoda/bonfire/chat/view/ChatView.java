package com.novoda.bonfire.chat.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novoda.bonfire.R;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.displayer.ChatDisplayer;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.notils.caster.Views;

public class ChatView extends LinearLayout implements ChatDisplayer {

    private final ChatAdapter chatAdapter;
    private TextView messageView;
    private ImageView submitButton;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private ChatActionListener actionListener;

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        chatAdapter = new ChatAdapter(LayoutInflater.from(context));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_chat_view, this);
        messageView = Views.findById(this, R.id.messageEdit);
        submitButton = Views.findById(this, R.id.submitButton);
        recyclerView = Views.findById(this, R.id.messagesRecyclerView);
        recyclerView.addItemDecoration(new ChatItemDecoration());
        toolbar = Views.findById(this, R.id.toolbar);
        toolbar.inflateMenu(R.menu.chat_menu);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    @Override
    public void attach(final ChatActionListener actionListener) {
        this.actionListener = actionListener;
        messageView.addTextChangedListener(textWatcher);
        submitButton.setOnClickListener(submitClickListener);
        toolbar.setNavigationOnClickListener(navigationClickListener);
        toolbar.setOnMenuItemClickListener(menuItemClickListener);
    }

    @Override
    public void detach(ChatActionListener actionListener) {
        submitButton.setOnClickListener(null);
        messageView.removeTextChangedListener(textWatcher);
        toolbar.setOnMenuItemClickListener(null);
        this.actionListener = null;
    }

    @Override
    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void showAddMembersButton() {
        toolbar.getMenu().findItem(R.id.manageOwners).setVisible(true);
    }

    @Override
    public void display(Chat chat, User user) {
        chatAdapter.update(chat, user);
        int lastMessagePosition = chatAdapter.getItemCount() == 0 ? 0 : chatAdapter.getItemCount() - 1;
        recyclerView.smoothScrollToPosition(lastMessagePosition);
    }

    @Override
    public void enableInteraction() {
        submitButton.setEnabled(true);
        submitButton.setColorFilter(null);
    }

    @Override
    public void disableInteraction() {
        submitButton.setEnabled(false);
        submitButton.setColorFilter(getResources().getColor(R.color.disabled_grey), PorterDuff.Mode.SRC_ATOP);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            actionListener.onMessageLengthChanged(s.length());
        }
    };

    private final OnClickListener submitClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            actionListener.onSubmitMessage(messageView.getText().toString());
            messageView.setText("");
        }
    };
    private final OnClickListener navigationClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            actionListener.onUpPressed();
        }
    };
    private Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.manageOwners) {
                actionListener.onManageOwnersClicked();
                return true;
            }
            return false;
        }
    };

    private class ChatItemDecoration extends RecyclerView.ItemDecoration {

        private final int horizontalMargin = getResources().getDimensionPixelOffset(R.dimen.chat_item_horizontal_margin);
        private final int verticalMargin = getResources().getDimensionPixelOffset(R.dimen.chat_item_vertical_margin);

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = horizontalMargin;
            outRect.right = horizontalMargin;
            outRect.top = verticalMargin;
            outRect.bottom = verticalMargin;
        }

    }
}
