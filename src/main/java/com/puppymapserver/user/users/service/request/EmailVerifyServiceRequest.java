package com.puppymapserver.user.users.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailVerifyServiceRequest {

    private final Long verificationId;
    private final String code;

    @Builder
    private EmailVerifyServiceRequest(Long verificationId, String code) {
        this.verificationId = verificationId;
        this.code = code;
    }
}
