package com.novoda.bonfire.link;

import com.novoda.bonfire.user.data.model.User;

import java.net.URI;

public interface LinkFactory {

    URI inviteLinkFrom(User user);

}
