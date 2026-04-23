package com.puppymapserver.user.users.controller.request;

import com.puppymapserver.user.users.service.request.PasswordResetServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetRequest {

    @NotNull(message = "인증 ID는 필수입니다.")
    private Long verificationId;

    @NotBlank(message = "인증번호는 필수입니다.")
    private String code;

    @NotNull(message = "변경할 비밀번호는 필수입니다.")
    private String newPassword;

    @Builder
    private PasswordResetRequest(Long verificationId, String code, String newPassword) {
        this.verificationId = verificationId;
        this.code = code;
        this.newPassword = newPassword;
    }

    public PasswordResetServiceRequest toServiceRequest() {
        return PasswordResetServiceRequest.builder()
                .verificationId(verificationId)
                .code(code)
                .newPassword(newPassword)
                .build();
    }
}
