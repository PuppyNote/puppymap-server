package com.puppymapserver.place.service.request;

import com.puppymapserver.place.entity.enums.PlaceCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceUpdateServiceRequest {
    private Long placeId;
    private Long userId;
    private String title;
    private String content;
    private PlaceCategory category;
    private Boolean largeDogAvailable;
    private Boolean parkingAvailable;
    private Boolean offLeashAvailable;
}
