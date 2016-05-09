package com.novoda.firechat.chat.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novoda.firechat.chat.model.Chat;
import com.novoda.firechat.chat.model.Message;

import java.util.ArrayList;

class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private final Context context;

    private Chat chat = new Chat(new ArrayList<Message>());

    ChatAdapter(Context context) {
        this.context = context;
        setHasStableIds(true);
    }

    public void update(Chat chat) {
        this.chat = chat;
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(new TextView(context));
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
