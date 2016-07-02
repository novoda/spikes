package com.novoda.enews;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class ApiPagedChannelHistory {

    @SerializedName("ok")
    public String ok;

    @SerializedName("messages")
    public List<ApiMessage> apiMessages;

    @SerializedName("has_more")
    public boolean hasMore;

    @Override
    public String toString() {
        return "ApiPagedChannelHistory{" +
                "ok='" + ok + '\'' +
                ", apiMessages=" + apiMessages +
                ", hasMore=" + hasMore +
                '}';
    }

    public static class ApiMessage {

        @SerializedName("text")
        public String text;

        @SerializedName("type")
        public String type;

        @SerializedName("ts")
        public String timeStamp;

        @Override
        public String toString() {
            return "\nApiMessage{" +
                    "text='" + text + '\'' +
                    ", type='" + type + '\'' +
                    ", timeStamp='" + timeStamp + '\'' +
                    '}';
        }
    }
}
