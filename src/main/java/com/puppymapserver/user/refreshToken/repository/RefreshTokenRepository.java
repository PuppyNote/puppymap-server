package com.puppymapserver.user.refreshToken.repository;

import com.puppymapserver.user.refreshToken.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void deleteAllInBatch();
}
