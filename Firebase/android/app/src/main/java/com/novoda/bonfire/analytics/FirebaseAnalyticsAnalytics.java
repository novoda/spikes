package com.novoda.bonfire.analytics;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import static com.google.firebase.analytics.FirebaseAnalytics.Event;
import static com.google.firebase.analytics.FirebaseAnalytics.Param;

public class FirebaseAnalyticsAnalytics implements Analytics {

    private static final String PARAM_CHANNEL_NAME = "channel_name";
    private static final String PARAM_SENDER = "sender";
    private static final String PARAM_USER_ID = "user_id";
    private static final String PARAM_ADDED_OWNER = "added_owner";
    private static final String PARAM_REMOVED_OWNER = "removed_owner";
    private static final String EVENT_SIGN_UP_SUCCESS = "sign_up_success";
    private static final String EVENT_MESSAGE_LENGTH = "message_length";
    private static final String EVENT_INVITE_OPENED = "invite_opened";
    private static final String EVENT_INVITE_ACCEPTED = "invite_accepted";
    private static final String EVENT_MANAGE_OWNERS = "manage_owners";
    private static final String EVENT_ADD_CHANNEL_OWNER = "add_channel_owner";
    private static final String EVENT_REMOVE_CHANNEL_OWNER = "remove_channel_owner";
    private static final String EVENT_SEND_INVITES = "send_invites";
    private static final String EVENT_CREATE_CHANNEL = "create_channel";
    private static final String CONTENT_TYPE_CHANNEL = "channel";

    private final FirebaseAnalytics firebaseAnalytics;

    public FirebaseAnalyticsAnalytics(FirebaseAnalytics firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
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
        firebaseAnalytics.logEvent(EVENT_SIGN_UP_SUCCESS, bundle);
    }

    @Override
    public void trackSelectChannel(String channelName) {
        Bundle bundle = new Bundle();
        bundle.putString(Param.CONTENT_TYPE, CONTENT_TYPE_CHANNEL);
        bundle.putString(Param.ITEM_ID, channelName);
        firebaseAnalytics.logEvent(Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void trackMessageLength(int messageLength, String userId, String channelName) {
        Bundle bundle = new Bundle();
        bundle.putInt(Param.VALUE, messageLength);
        bundle.putString(PARAM_CHANNEL_NAME, channelName);
        bundle.putString(PARAM_USER_ID, userId);
        firebaseAnalytics.logEvent(EVENT_MESSAGE_LENGTH, bundle);
    }

    @Override
    public void trackInvitationOpened(String senderId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_SENDER, senderId);
        firebaseAnalytics.logEvent(EVENT_INVITE_OPENED, bundle);
    }

    @Override
    public void trackInvitationAccepted(String senderId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_SENDER, senderId);
        firebaseAnalytics.logEvent(EVENT_INVITE_ACCEPTED, bundle);
    }

    @Override
    public void trackManageOwners(String userId, String channelName) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_USER_ID, userId);
        bundle.putString(PARAM_CHANNEL_NAME, channelName);
        firebaseAnalytics.logEvent(EVENT_MANAGE_OWNERS, bundle);
    }

    @Override
    public void trackAddChannelOwner(String channelName, String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_CHANNEL_NAME, channelName);
        bundle.putString(PARAM_ADDED_OWNER, userId);
        firebaseAnalytics.logEvent(EVENT_ADD_CHANNEL_OWNER, bundle);
    }

    @Override
    public void trackRemoveChannelOwner(String channelName, String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_CHANNEL_NAME, channelName);
        bundle.putString(PARAM_REMOVED_OWNER, userId);
        firebaseAnalytics.logEvent(EVENT_REMOVE_CHANNEL_OWNER, bundle);
    }

    @Override
    public void trackSendInvitesSelected(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_USER_ID, userId);
        firebaseAnalytics.logEvent(EVENT_SEND_INVITES, bundle);
    }

    @Override
    public void trackCreateChannel(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_USER_ID, userId);
        firebaseAnalytics.logEvent(EVENT_CREATE_CHANNEL, bundle);
    }

}
