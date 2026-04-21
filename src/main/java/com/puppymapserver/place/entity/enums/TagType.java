package com.puppymapserver.place.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TagType {
    CONSTRUCTION("공사중"),
    TICK("진드기주의"),
    DANGEROUS("위험"),
    CLOSED("폐쇄"),
    ETC("기타");

    private final String text;
}
