package com.novoda.enews;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HistoryConverter {

    public ChannelHistory convert(List<ApiPagedChannelHistory> apiPagedChannelHistory) {
        List<ChannelHistory.Message> messages = new ArrayList<>();
        for (ApiPagedChannelHistory pagedChannelHistory : apiPagedChannelHistory) {
            for (ApiPagedChannelHistory.ApiMessage apiMessage : pagedChannelHistory.apiMessages) {
                try {
                    messages.add(convert(apiMessage));
                } catch (ConvertException e) {
                    System.err.println("Skipping with error " + e);
                    continue;
                }
            }
        }
        LocalDateTime historyFrom = getOldestMessageLocalDateTime(apiPagedChannelHistory.get(apiPagedChannelHistory.size() - 1));
        LocalDateTime historyTo = getLatestMessageLocalDateTime(apiPagedChannelHistory.get(0));
        return new ChannelHistory(historyFrom, historyTo, messages);
    }

    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    private static ChannelHistory.Message convert(ApiPagedChannelHistory.ApiMessage apiMessage) throws ConvertException {
        List<ApiPagedChannelHistory.ApiAttachment> attachments = apiMessage.attachments;
        String imageUrl = null;
        if (attachments != null) {
            imageUrl = attachments.get(0).imageUrl;
        }
        String text = apiMessage.text; // Bots send messages with attachments but no text

        if (text == null) {
            throw new ConvertException("Cannot convert " + apiMessage, new NullPointerException("text was null"));
        }

        String pageLink = "";
        Matcher matcher = urlPattern.matcher(text);
        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            // now you have the offsets of a URL match
            pageLink = text.substring(matchStart, matchEnd);
        }

        return new ChannelHistory.Message(text, imageUrl, pageLink);
    }

    private static class ConvertException extends Exception {

        ConvertException(String message, Throwable cause) {
            super(message, cause);
        }
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
