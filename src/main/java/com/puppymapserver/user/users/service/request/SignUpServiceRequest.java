package com.puppymapserver.user.users.service.request;

import com.puppymapserver.user.users.entity.User;
import com.puppymapserver.user.users.entity.enums.Role;
import com.puppymapserver.user.users.entity.enums.SnsType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignUpServiceRequest {
    private final String email;
    private final String password;
    private final String nickName;

    @Builder
    private SignUpServiceRequest(String email, String password, String nickName) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
    }

    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .nickName(nickName)
                .snsType(SnsType.NORMAL)
                .role(Role.USER)
                .useYn("Y")
                .build();
    }
}
