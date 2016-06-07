package com.novoda.bonfire.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.novoda.bonfire.R;
import com.novoda.bonfire.chat.data.model.Chat;
import com.novoda.bonfire.chat.data.model.Message;

import java.util.ArrayList;

class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private Chat chat = new Chat(new ArrayList<Message>());

    ChatAdapter() {
        setHasStableIds(true);
    }

    public void update(Chat chat) {
        this.chat = chat;
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MessageViewHolder((MessageView) inflater.inflate(R.layout.message_item_layout, parent, false));
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

}
