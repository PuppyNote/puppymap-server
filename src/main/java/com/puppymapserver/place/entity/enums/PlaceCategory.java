package com.puppymapserver.place.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaceCategory {
    PARK("공원"),
    TRAIL("산책로"),
    CAFE("카페"),
    ETC("기타");

    private final String text;
}
