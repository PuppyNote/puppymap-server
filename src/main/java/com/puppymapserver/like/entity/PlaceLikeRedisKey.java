package com.puppymapserver.like.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaceLikeRedisKey {

    USER_LIKED("user:liked:"),                            // user:liked:{userId}:{placeId} → "1"/"0"
    PLACE_LIKE_COUNT("place:like:count:"),                // place:like:count:{placeId} → 총 좋아요 수
    DIRTY("place:like:dirty"),                            // place:like:dirty → Set<placeId>
    DIRTY_PROCESSING("place:like:dirty:processing:"),     // place:like:dirty:processing:{ts}
    DELTA_ADD("place:like:delta:add:"),                   // place:like:delta:add:{placeId} → 새로 좋아요한 userId Set
    DELTA_REMOVE("place:like:delta:remove:"),             // place:like:delta:remove:{placeId} → 좋아요 취소한 userId Set
    DELTA_ADD_PROCESSING("place:like:delta:add:processing:"),
    DELTA_REMOVE_PROCESSING("place:like:delta:remove:processing:");

    private final String key;

    public String of(Object... args) {
        StringBuilder sb = new StringBuilder(key);
        for (Object arg : args) {
            sb.append(arg).append(":");
        }
        if (args.length > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
