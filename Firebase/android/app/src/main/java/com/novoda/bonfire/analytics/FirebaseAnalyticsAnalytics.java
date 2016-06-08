package com.novoda.bonfire.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import static com.google.firebase.analytics.FirebaseAnalytics.*;

public class FirebaseAnalyticsAnalytics implements Analytics {

    private static final String CHANNEL_NAME = "channel_name";
    private static final String SENDER = "sender";
    private static final String USER_ID = "user_id";
    private final FirebaseAnalytics firebaseAnalytics;

    public FirebaseAnalyticsAnalytics(Context context) {
        firebaseAnalytics = getInstance(context);
    }

    @Override
    public void trackSignInStarted(String method) {
        Bundle bundle = new Bundle();
        bundle.putString(Param.SIGN_UP_METHOD, method);
        firebaseAnalytics.logEvent(Event.SIGN_UP, bundle);
    }

    @Override
    public void trackSignInSuccessful(String method) {
        Bundle bundle = new Bundle();
        bundle.putString(Param.SIGN_UP_METHOD, method);
        firebaseAnalytics.logEvent("sign_up_success", bundle);
    }

    @Override
    public void trackSelectChannel(String channelName) {
        Bundle bundle = new Bundle();
        bundle.putString(Param.CONTENT_TYPE, "channel");
        bundle.putString(Param.ITEM_ID, channelName);
        firebaseAnalytics.logEvent(Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void trackMessageLength(int messageLength, String userId, String channelName) {
        Bundle bundle = new Bundle();
        bundle.putInt(Param.VALUE, messageLength);
        bundle.putString(CHANNEL_NAME, channelName);
        bundle.putString(USER_ID, userId);
        firebaseAnalytics.logEvent("message_length", bundle);
    }

    @Override
    public void trackInvitationOpened(String senderId) {
        Bundle bundle = new Bundle();
        bundle.putString(SENDER, senderId);
        firebaseAnalytics.logEvent("invite_opened", bundle);
    }

    @Override
    public void trackInvitationAccepted(String senderId) {
        Bundle bundle = new Bundle();
        bundle.putString(SENDER, senderId);
        firebaseAnalytics.logEvent("invite_accepted", bundle);
    }

    @Override
    public void trackManageOwners(String userId, String channelName) {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        bundle.putString(CHANNEL_NAME, channelName);
        firebaseAnalytics.logEvent("manage_owners", bundle);
    }

    @Override
    public void trackAddChannelOwner(String channelName, String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(CHANNEL_NAME, channelName);
        bundle.putString("added_owner", userId);
        firebaseAnalytics.logEvent("add_channel_owner", bundle);
    }

    @Override
    public void trackRemoveChannelOwner(String channelName, String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(CHANNEL_NAME, channelName);
        bundle.putString("removed_owner", userId);
        firebaseAnalytics.logEvent("remove_channel_owner", bundle);
    }

    @Override
    public void trackSendInvitesSelected(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        firebaseAnalytics.logEvent("send_invites", bundle);
    }

    @Override
    public void trackCreateChannel(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        firebaseAnalytics.logEvent("create_channel", bundle);
    }

}
