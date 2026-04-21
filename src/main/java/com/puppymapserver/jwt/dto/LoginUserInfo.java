package com.puppymapserver.jwt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginUserInfo {
    private Long userId;
    private String role;

    @Builder
    private LoginUserInfo(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public static LoginUserInfo of(Long userId, String role) {
        return LoginUserInfo.builder()
            .userId(userId)
            .role(role)
            .build();
    }
}
