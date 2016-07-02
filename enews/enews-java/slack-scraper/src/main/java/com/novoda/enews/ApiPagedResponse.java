package com.novoda.enews;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

class ApiPagedResponse {
    private final List<ApiPagedChannelHistory> responses = new ArrayList<>();

    private LocalDateTime lastResponseDateTime;
    private boolean hasMore = false;

    public ApiPagedResponse(LocalDateTime start) {
        this.lastResponseDateTime = start;
    }

    public boolean hasMoreResponses() {
        return hasMore;
    }

    public void addResponse(ApiPagedChannelHistory apiPagedChannelHistory) {
        updateLastResponseDateTime(apiPagedChannelHistory);
        responses.add(apiPagedChannelHistory);
    }

    private void updateLastResponseDateTime(ApiPagedChannelHistory apiPagedChannelHistory) {
        List<ApiPagedChannelHistory.ApiMessage> apiMessages = apiPagedChannelHistory.apiMessages;
        ApiPagedChannelHistory.ApiMessage oldestApiMessage = apiMessages.get(apiMessages.size() - 1);
        String lastResponseEpochTime = oldestApiMessage.timeStamp;
        lastResponseDateTime = convertToLocalDateTime(lastResponseEpochTime);
        hasMore = apiPagedChannelHistory.hasMore;
    }

    private LocalDateTime convertToLocalDateTime(String lastResponseEpochTime) {
        int decimalSplit = lastResponseEpochTime.indexOf(".");
        Long epochSecond = Long.valueOf(lastResponseEpochTime.substring(0, decimalSplit));
        Integer nanoOfSecond = Integer.valueOf(lastResponseEpochTime.substring(decimalSplit + 1));
        ZoneOffset timezone = ZoneOffset.UTC;
        return LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, timezone);
    }

    public String getLastResponseEpochTime() {
        return String.valueOf(lastResponseDateTime.toEpochSecond(ZoneOffset.UTC));
    }

    public List<ApiPagedChannelHistory> getResponses() {
        return responses;
    }

    public boolean responsesAreAfter(LocalDateTime dateTime) {
        List<ApiPagedChannelHistory.ApiMessage> latestMessages = responses.get(responses.size() - 1).apiMessages;
        ApiPagedChannelHistory.ApiMessage latestMessage = latestMessages.get(latestMessages.size() - 1);
        LocalDateTime latestMessageDateTime = convertToLocalDateTime(latestMessage.timeStamp);
        return latestMessageDateTime.isAfter(dateTime);
    }
}
