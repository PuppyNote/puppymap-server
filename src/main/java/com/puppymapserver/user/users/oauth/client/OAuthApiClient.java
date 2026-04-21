package com.puppymapserver.user.users.oauth.client;

import com.puppymapserver.user.users.entity.enums.SnsType;

public interface OAuthApiClient {
    SnsType oAuthSnsType();
    String getEmail(String code);
}
