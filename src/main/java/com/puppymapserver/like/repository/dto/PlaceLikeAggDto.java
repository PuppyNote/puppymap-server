package com.puppymapserver.like.repository.dto;

import lombok.Getter;

@Getter
public class PlaceLikeAggDto {

    private final Long placeId;
    private final Long likeCount;
    private final Long isLikedCount;

    public PlaceLikeAggDto(Long placeId, Long likeCount, Long isLikedCount) {
        this.placeId = placeId;
        this.likeCount = likeCount != null ? likeCount : 0L;
        this.isLikedCount = isLikedCount != null ? isLikedCount : 0L;
    }
}
