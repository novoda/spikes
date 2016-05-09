package com.novoda.firechat.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.novoda.firechat.chat.model.Message;

class MessageViewHolder extends RecyclerView.ViewHolder {

    public MessageViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(Message message) {
        ((TextView) itemView).setText(message.toString());
    }

}
