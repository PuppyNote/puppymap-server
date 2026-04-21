package com.puppymapserver.place.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaceStatus {
    PENDING("승인대기"),
    APPROVED("승인"),
    REJECTED("거절");

    private final String text;
}
