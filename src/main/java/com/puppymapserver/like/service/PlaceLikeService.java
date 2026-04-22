package com.puppymapserver.like.service;

import com.puppymapserver.like.service.response.PlaceLikeToggleResponse;

public interface PlaceLikeService {
    PlaceLikeToggleResponse toggleLike(Long placeId);
}
