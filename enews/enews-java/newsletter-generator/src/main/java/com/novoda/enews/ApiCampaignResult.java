package com.novoda.enews;

import com.google.gson.annotations.SerializedName;

class ApiCampaignResult {

    @SerializedName("text")
    public String id;

    @Override
    public String toString() {
        return "ApiCampaignResult{" +
                "id='" + id + '\'' +
                '}';
    }
}
