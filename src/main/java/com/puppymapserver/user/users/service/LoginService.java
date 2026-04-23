package com.puppymapserver.user.users.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.puppymapserver.user.users.service.request.EmailSendServiceRequest;
import com.puppymapserver.user.users.service.request.LoginServiceRequest;
import com.puppymapserver.user.users.service.request.OAuthLoginServiceRequest;
import com.puppymapserver.user.users.service.request.PasswordResetServiceRequest;
import com.puppymapserver.user.users.service.request.TokenRefreshServiceRequest;
import com.puppymapserver.user.users.service.response.LoginResponse;
import com.puppymapserver.user.users.service.response.OAuthLoginResponse;
import com.puppymapserver.user.users.service.response.TokenRefreshResponse;

public interface LoginService {

    LoginResponse normalLogin(LoginServiceRequest loginServiceRequest) throws JsonProcessingException;

    OAuthLoginResponse oauthLogin(OAuthLoginServiceRequest oAuthLoginServiceRequest) throws
            JsonProcessingException;

    TokenRefreshResponse refresh(TokenRefreshServiceRequest request);

    void resetPassword(PasswordResetServiceRequest request);

    Long sendPasswordResetEmail(EmailSendServiceRequest request);

}
