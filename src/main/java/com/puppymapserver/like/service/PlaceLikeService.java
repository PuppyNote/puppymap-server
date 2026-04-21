package com.puppymapserver.like.service;

public interface PlaceLikeService {
    void like(Long placeId, Long userId);
    void unlike(Long placeId, Long userId);
}
