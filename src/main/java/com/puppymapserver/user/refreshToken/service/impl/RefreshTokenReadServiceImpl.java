package com.puppymapserver.user.refreshToken.service.impl;

import com.puppymapserver.global.exception.PuppyMapException;
import com.puppymapserver.user.refreshToken.entity.RefreshToken;
import com.puppymapserver.user.refreshToken.repository.RefreshTokenRepository;
import com.puppymapserver.user.refreshToken.service.RefreshTokenReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenReadServiceImpl implements RefreshTokenReadService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new PuppyMapException("유효하지 않은 RefreshToken입니다."));
    }
}
