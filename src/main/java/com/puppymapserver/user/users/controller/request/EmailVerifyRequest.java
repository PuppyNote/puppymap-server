package com.puppymapserver.user.users.controller.request;

import com.puppymapserver.user.users.service.request.EmailVerifyServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerifyRequest {

    @NotNull(message = "인증 ID는 필수입니다.")
    private Long verificationId;

    @NotBlank(message = "인증번호는 필수입니다.")
    private String code;

    public EmailVerifyServiceRequest toServiceRequest() {
        return EmailVerifyServiceRequest.builder()
                .verificationId(verificationId)
                .code(code)
                .build();
    }
}
