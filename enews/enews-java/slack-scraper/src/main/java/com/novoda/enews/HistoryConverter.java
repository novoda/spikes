package com.novoda.enews;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

class HistoryConverter {

    public ChannelHistory convert(List<ApiPagedChannelHistory> apiPagedChannelHistory) {
        List<ChannelHistory.Message> messages = new ArrayList<>();
        for (ApiPagedChannelHistory pagedChannelHistory : apiPagedChannelHistory) {
            for (ApiPagedChannelHistory.ApiMessage apiMessage : pagedChannelHistory.apiMessages) {
                messages.add(convert(apiMessage));
            }
        }
        LocalDateTime historyFrom = getOldestMessageLocalDateTime(apiPagedChannelHistory.get(apiPagedChannelHistory.size() - 1));
        LocalDateTime historyTo = getLatestMessageLocalDateTime(apiPagedChannelHistory.get(0));
        return new ChannelHistory(historyFrom, historyTo, messages);
    }

    private static ChannelHistory.Message convert(ApiPagedChannelHistory.ApiMessage apiMessage) {
        List<ApiPagedChannelHistory.ApiAttachment> attachments = apiMessage.attachments;
        String imageUrl = null;
        if (attachments != null) {
            imageUrl = attachments.get(0).imageUrl;
        }
        if (imageUrl == null) {
            imageUrl = "http://www.emmys.com/sites/default/files/The-Missing.jpg";
        }
        String text = apiMessage.text; // Bots send messages with attachments but no text
        return new ChannelHistory.Message(text == null ? "" : text, imageUrl);
    }

    private static LocalDateTime getOldestMessageLocalDateTime(ApiPagedChannelHistory apiPagedChannelHistory) {
        List<ApiPagedChannelHistory.ApiMessage> apiMessages = apiPagedChannelHistory.apiMessages;
        ApiPagedChannelHistory.ApiMessage lastMessage = apiMessages.get(apiMessages.size() - 1);
        return convertToLocalDateTime(lastMessage.timeStamp);
    }

    private static LocalDateTime getLatestMessageLocalDateTime(ApiPagedChannelHistory apiPagedChannelHistory) {
        List<ApiPagedChannelHistory.ApiMessage> apiMessages = apiPagedChannelHistory.apiMessages;
        ApiPagedChannelHistory.ApiMessage firstMessage = apiMessages.get(0);
        return convertToLocalDateTime(firstMessage.timeStamp);
    }

    private static LocalDateTime convertToLocalDateTime(String lastResponseEpochTime) {
        int decimalSplit = lastResponseEpochTime.indexOf(".");
        Long epochSecond = Long.valueOf(lastResponseEpochTime.substring(0, decimalSplit));
        Integer nanoOfSecond = Integer.valueOf(lastResponseEpochTime.substring(decimalSplit + 1));
        ZoneOffset timezone = ZoneOffset.UTC;
        return LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, timezone);
    }
}
