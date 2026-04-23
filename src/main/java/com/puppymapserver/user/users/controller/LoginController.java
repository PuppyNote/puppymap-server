package com.puppymapserver.user.users.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.puppymapserver.global.ApiResponse;
import com.puppymapserver.user.users.controller.request.EmailSendRequest;
import com.puppymapserver.user.users.controller.request.LoginOauthRequest;
import com.puppymapserver.user.users.controller.request.LoginRequest;
import com.puppymapserver.user.users.controller.request.PasswordResetRequest;
import com.puppymapserver.user.users.controller.request.TokenRefreshRequest;
import com.puppymapserver.user.users.service.LoginService;
import com.puppymapserver.user.users.service.response.LoginResponse;
import com.puppymapserver.user.users.service.response.OAuthLoginResponse;
import com.puppymapserver.user.users.service.response.TokenRefreshResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) throws JsonProcessingException {
        return ApiResponse.ok(loginService.normalLogin(loginRequest.toServiceRequest()));
    }

    @PostMapping("/oauth/login")
    public ApiResponse<OAuthLoginResponse> oauthLogin(@Valid @RequestBody LoginOauthRequest loginOauthRequest) throws JsonProcessingException {
        return ApiResponse.ok(loginService.oauthLogin(loginOauthRequest.toServiceRequest()));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenRefreshResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ApiResponse.ok(loginService.refresh(request.toServiceRequest()));
    }

    @PostMapping("/password/email/send")
    public ApiResponse<Long> sendPasswordResetEmail(@Valid @RequestBody EmailSendRequest request) {
        return ApiResponse.ok(loginService.sendPasswordResetEmail(request.toServiceRequest()));
    }

    @PostMapping("/password/reset")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        loginService.resetPassword(request.toServiceRequest());
        return ApiResponse.ok(null);
    }

}
