package com.novoda.priorityshare.composer;

import android.os.Bundle;

/**
 * Defines a message composer strategy that can be used to compose
 * a text to be shared following certain requirements (e.g., enforcing
 * a maximum text length).
 */
public interface MessageComposer {

    Bundle composeToBundle(String subject, String url);

}
