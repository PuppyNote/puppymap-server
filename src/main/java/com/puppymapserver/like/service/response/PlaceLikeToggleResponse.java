package com.puppymapserver.like.service.response;

import lombok.Getter;

@Getter
public class PlaceLikeToggleResponse {

    private final boolean liked;
    private final long likeCount;

    private PlaceLikeToggleResponse(boolean liked, long likeCount) {
        this.liked = liked;
        this.likeCount = likeCount;
    }

    public static PlaceLikeToggleResponse of(boolean liked, long likeCount) {
        return new PlaceLikeToggleResponse(liked, likeCount);
    }
}
