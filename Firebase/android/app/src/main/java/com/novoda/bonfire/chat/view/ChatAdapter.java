package com.novoda.bonfire.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.novoda.bonfire.R;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;
import com.novoda.bonfire.user.data.model.User;

import java.util.ArrayList;

class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {

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
        if (viewType == 0) {
            return new MessageViewHolder((MessageView) inflater.inflate(R.layout.self_message_item_layout, parent, false));
        } else {
            return new MessageViewHolder((MessageView) inflater.inflate(R.layout.message_item_layout, parent, false));
        }
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
        return chat.get(position).getAuthor().getId().equals(user.getId()) ? 0 : 1;
    }
}
