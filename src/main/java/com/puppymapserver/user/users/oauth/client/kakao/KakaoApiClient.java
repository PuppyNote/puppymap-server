package com.puppymapserver.user.users.oauth.client.kakao;

import org.springframework.stereotype.Component;

import com.puppymapserver.user.users.oauth.client.OAuthApiClient;
import com.puppymapserver.user.users.entity.enums.SnsType;
import com.puppymapserver.user.users.oauth.feign.kakao.KakaoApiFeignCall;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoApiClient implements OAuthApiClient {

    private final KakaoApiFeignCall kakaoApiFeignCall;

    @Override
    public SnsType oAuthSnsType() {
        return SnsType.KAKAO;
    }

    @Override
    public String getEmail(String accessToken){
        return kakaoApiFeignCall.getUserInfo("Bearer " + accessToken).getEmail();
    }
}
