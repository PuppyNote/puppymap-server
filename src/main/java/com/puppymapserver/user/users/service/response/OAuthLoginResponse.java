package com.puppymapserver.user.users.service.response;

import com.puppymapserver.jwt.dto.JwtToken;
import com.puppymapserver.user.users.entity.User;
import com.puppymapserver.user.users.entity.enums.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthLoginResponse {

	private final String email;
	private final String accessToken;
	private final String refreshToken;
	private final Role role;

	@Builder
	private OAuthLoginResponse(String email, String accessToken, String refreshToken, Role role) {
		this.email = email;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.role = role;
	}

	public static OAuthLoginResponse of(User user, JwtToken jwtToken) {
		return OAuthLoginResponse.builder()
			.email(user.getEmail())
			.accessToken(jwtToken.getAccessToken())
			.refreshToken(jwtToken.getRefreshToken())
			.role(user.getRole())
			.build();
	}
}
