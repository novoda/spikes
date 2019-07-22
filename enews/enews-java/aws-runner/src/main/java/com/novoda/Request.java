package com.novoda;

public class Request {

    String slackToken;
    String mailChimpApiKey;

    public Request() {
        // aws use
    }

    public Request(String slackToken, String mailChimpApiKey) {
        this.slackToken = slackToken;
        this.mailChimpApiKey = mailChimpApiKey;
    }

    public void setSlackToken(String slackToken) {
        this.slackToken = slackToken;
    }

    public void setMailChimpApiKey(String mailChimpApiKey) {
        this.mailChimpApiKey = mailChimpApiKey;
    }

    public String getSlackToken() {
        return slackToken;
    }

    public String getMailChimpApiKey() {
        return mailChimpApiKey;
    }
}
