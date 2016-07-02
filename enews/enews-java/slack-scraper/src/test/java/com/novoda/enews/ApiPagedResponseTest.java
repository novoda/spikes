package com.novoda.enews;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiPagedResponseTest {

    public static final String TWENTY16_DEC_1_0_0_0 = "1480550400.000000";
    public static final String TWENTY16_DEC_3_0_0_0 = "1480723200.000000";
    public static final LocalDateTime TWENTY16_DEC_12_2_0_0_0 = LocalDateTime.of(2016, 12, 2, 0, 0, 0);

    @Test
    public void responseThatHasMessagesThatAreOlderThanTestDate_returnFalse() throws Exception {
        ApiPagedChannelHistory apiPagedChannelHistory = createApiPagedChannelHistoryWithLastResponseDate(TWENTY16_DEC_1_0_0_0);
        ApiPagedResponse response = new ApiPagedResponse(null);
        response.addResponse(apiPagedChannelHistory);

        assertThat(response.responsesAreAfter(TWENTY16_DEC_12_2_0_0_0)).isEqualTo(false);
    }

    @Test
    public void responseThatHasMessagesThatAreYoungerThanTestDate_returnTrue() throws Exception {
        ApiPagedChannelHistory apiPagedChannelHistory = createApiPagedChannelHistoryWithLastResponseDate(TWENTY16_DEC_3_0_0_0);
        ApiPagedResponse response = new ApiPagedResponse(null);
        response.addResponse(apiPagedChannelHistory);

        assertThat(response.responsesAreAfter(TWENTY16_DEC_12_2_0_0_0)).isEqualTo(true);
    }

    private ApiPagedChannelHistory createApiPagedChannelHistoryWithLastResponseDate(String timestamp) {
        ApiPagedChannelHistory apiPagedChannelHistory = new ApiPagedChannelHistory();
        apiPagedChannelHistory.apiMessages = new ArrayList<>();
        ApiPagedChannelHistory.ApiMessage apiMessage = new ApiPagedChannelHistory.ApiMessage();
        apiMessage.timeStamp = timestamp;
        apiPagedChannelHistory.apiMessages.add(apiMessage);
        return apiPagedChannelHistory;
    }
}