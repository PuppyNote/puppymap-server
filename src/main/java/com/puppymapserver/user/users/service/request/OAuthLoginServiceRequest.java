package com.puppymapserver.user.users.service.request;

import com.puppymapserver.user.users.entity.enums.SnsType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class OAuthLoginServiceRequest {
	private final String token;
	private final SnsType snsType;

	@Builder
	private OAuthLoginServiceRequest(String token, SnsType snsType) {
		this.token = token;
		this.snsType = snsType;
	}
}
