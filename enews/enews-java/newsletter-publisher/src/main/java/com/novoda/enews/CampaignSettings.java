package com.novoda.enews;

class CampaignSettings {

    private final String listId;
    private final String subjectLine;
    private final String fromName;
    private final String replyToEmail;

    public CampaignSettings(String listId, String subjectLine, String fromName, String replyToEmail) {
        this.listId = listId;
        this.subjectLine = subjectLine;
        this.fromName = fromName;
        this.replyToEmail = replyToEmail;
    }

    public String getListId() {
        return listId;
    }

    public String getSubjectLine() {
        return subjectLine;
    }

    public String getFromName() {
        return fromName;
    }

    public String getReplyToEmail() {
        return replyToEmail;
    }

}
