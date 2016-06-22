package com.novoda.bonfire.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.novoda.bonfire.R;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.view.MessageBubbleDrawable;
import com.novoda.notils.exception.DeveloperError;

import java.util.ArrayList;

import static com.novoda.bonfire.view.MessageBubbleDrawable.*;

class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_THIS_USER = 0;
    private static final int VIEW_TYPE_MESSAGE_OTHER_USERS = 1;
    private Chat chat = new Chat(new ArrayList<Message>());
    private User user = new User("", "", "");
    private final LayoutInflater inflater;

    ChatAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        setHasStableIds(true);
    }

    public void update(Chat chat, User user) {
        this.chat = chat;
        this.user = user;
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MessageBubbleDrawable bubbleDrawable;
        MessageView messageView;
        if (viewType == VIEW_TYPE_MESSAGE_THIS_USER) {
            bubbleDrawable = new MessageBubbleDrawable(parent.getContext(), R.color.colorPrimaryLight, Gravity.END);
            messageView = (MessageView) inflater.inflate(R.layout.self_message_item_layout, parent, false);
        } else if (viewType == VIEW_TYPE_MESSAGE_OTHER_USERS) {
            bubbleDrawable = new MessageBubbleDrawable(parent.getContext(), R.color.bubble_grey, Gravity.START);
            messageView = (MessageView) inflater.inflate(R.layout.message_item_layout, parent, false);
        } else {
            throw new DeveloperError("There is an unknown view type, you should inflate a view for it.");
        }
        messageView.setTextBackground(bubbleDrawable);
        return new MessageViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.bind(chat.get(position));
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    @Override
    public long getItemId(int position) {
        return chat.get(position).getTimestamp();
    }

    @Override
    public int getItemViewType(int position) {
        return chat.get(position).getAuthor().getId().equals(user.getId()) ? VIEW_TYPE_MESSAGE_THIS_USER : VIEW_TYPE_MESSAGE_OTHER_USERS;
    }
}
