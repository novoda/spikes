package com.novoda.enews;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

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

        @Nullable
        @SerializedName("attachments")
        public List<ApiAttachment> attachments;

        @Override
        public String toString() {
            return "ApiMessage{" +
                    "text='" + text + '\'' +
                    ", type='" + type + '\'' +
                    ", timeStamp='" + timeStamp + '\'' +
                    ", attachments=" + attachments +
                    '}';
        }
    }

    public static class ApiAttachment {

        @SerializedName("image_url")
        public String imageUrl;

        @SerializedName("thumb_url")
        public String thumbnailUrl;

        @Override
        public String toString() {
            return "ApiAttachment{" +
                    "imageUrl='" + imageUrl + '\'' +
                    ", thumbnailUrl='" + thumbnailUrl + '\'' +
                    '}';
        }
    }
}
