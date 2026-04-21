package com.puppymapserver.user.refreshToken.service;

import com.puppymapserver.user.refreshToken.entity.RefreshToken;

public interface RefreshTokenReadService {

    RefreshToken findByRefreshToken(String refreshToken);
}
