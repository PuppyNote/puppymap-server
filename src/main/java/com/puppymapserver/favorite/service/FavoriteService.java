package com.puppymapserver.favorite.service;

import com.puppymapserver.favorite.service.response.FavoriteResponse;

import java.util.List;

public interface FavoriteService {
    void add(Long placeId, Long userId);
    void remove(Long placeId, Long userId);
    List<FavoriteResponse> getMyFavorites(Long userId);
}
