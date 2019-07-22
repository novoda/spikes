package com.novoda.enews;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

class MockSlackWebService implements SlackWebService {
    @Override
    public Call<ApiPagedChannelHistory> getChannelHistory(String token, String channel, String latest, int count) {
        return new Call<ApiPagedChannelHistory>() {
            @Override
            public Response<ApiPagedChannelHistory> execute() throws IOException {
                ApiPagedChannelHistory body = new ApiPagedChannelHistory();

                body.ok = "ok";
                body.hasMore = false;
                List<ApiPagedChannelHistory.ApiMessage> apiMessages = new ArrayList<>();

                addEnewsMessage(apiMessages, "Hello World lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum 1");
                addStandardMessage(apiMessages, "Hello World 2");
                addEnewsMessage(apiMessages, "Hello World foo bar foo bar foo bar foo bar foo bar 3");
                addStandardMessage(apiMessages, "Hello World 4");
                addEnewsMessage(apiMessages, "Hello World 5");

                body.apiMessages = apiMessages;

                return Response.success(body);
            }

            private void addEnewsMessage(List<ApiPagedChannelHistory.ApiMessage> apiMessages, String text) {
                addStandardMessage(apiMessages, text + " <#C1V389HGB|enews>" + " http://novoda.com");
            }

            private void addStandardMessage(List<ApiPagedChannelHistory.ApiMessage> apiMessages, String text) {
                ApiPagedChannelHistory.ApiMessage message = new ApiPagedChannelHistory.ApiMessage();

                message.text = text;
                message.timeStamp = String.valueOf((System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)) / 1000) + ".0";
                message.type = "type";
                ApiPagedChannelHistory.ApiAttachment attachment = new ApiPagedChannelHistory.ApiAttachment();
                attachment.imageUrl = "https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/missing-image.png";
                attachment.thumbnailUrl = "https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/missing-image.png";
                message.attachments = Collections.singletonList(attachment);

                apiMessages.add(message);
            }

            @Override
            public void enqueue(Callback<ApiPagedChannelHistory> callback) {

            }

            @Override
            public boolean isExecuted() {
                return true;
            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public Call<ApiPagedChannelHistory> clone() {
                return this;
            }

            @Override
            public Request request() {
                return null;
            }
        };
    }
}
