package com.novoda.enews;

import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

class SlackHistoryFetcher {

    private static final String CHANNEL_GENERAL = "C029J9QTH";
    private static final int NUMBER_OF_RESULTS = 200;

    private final SlackWebService slackWebService;
    private final String slackToken;
    private final String channel;
    private final HistoryConverter historyConverter;

    public static SlackHistoryFetcher from(SlackWebService slackWebService, String slackToken) {
        HistoryConverter historyConverter = new HistoryConverter();
        return new SlackHistoryFetcher(slackWebService, slackToken, CHANNEL_GENERAL, historyConverter);
    }

    SlackHistoryFetcher(SlackWebService slackWebService,
                        String slackToken,
                        String channel, HistoryConverter historyConverter) {
        this.slackWebService = slackWebService;
        this.slackToken = slackToken;
        this.channel = channel;
        this.historyConverter = historyConverter;
    }

    public ChannelHistory getChannelHistory(LocalDateTime start, LocalDateTime end) {
        return historyConverter.convert(getApiChannelHistory(start, end));
    }

    private List<ApiPagedChannelHistory> getApiChannelHistory(LocalDateTime start, LocalDateTime end) {
        ApiPagedResponse apiPagedResponse = new ApiPagedResponse(start);
        do {
            appendChannelHistory(apiPagedResponse);
        } while (apiPagedResponse.responsesAreAfter(end) && apiPagedResponse.hasMoreResponses());
        return apiPagedResponse.getResponses();
    }

    private void appendChannelHistory(ApiPagedResponse apiPagedResponse) {
        Response<ApiPagedChannelHistory> response = fetchApiChannelHistoryResponse(apiPagedResponse.getLastResponseEpochTime());
        if (response.isSuccessful()) {
            ApiPagedChannelHistory apiPagedChannelHistory = response.body();
            apiPagedResponse.addResponse(apiPagedChannelHistory);
        } else {
            throw new IllegalStateException("No internet or server down or something."
                    + " code: " + response.code()
                    + " error: " + response.body());
        }
    }

    private Response<ApiPagedChannelHistory> fetchApiChannelHistoryResponse(String lastResponseEpochTime) {
        Call<ApiPagedChannelHistory> channelHistory = slackWebService
                .getChannelHistory(slackToken, channel, lastResponseEpochTime, NUMBER_OF_RESULTS);
        try {
            return channelHistory.execute();
        } catch (IOException e) {
            throw new IllegalStateException("FooBar ", e);
        }
    }

}
