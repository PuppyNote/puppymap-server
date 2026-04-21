package com.puppymapserver.place.service.request;

import com.puppymapserver.place.entity.enums.PlaceCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceCreateServiceRequest {
    private String title;
    private String content;
    private Double latitude;
    private Double longitude;
    private PlaceCategory category;
    private Boolean largeDogAvailable;
    private Boolean parkingAvailable;
    private Boolean offLeashAvailable;
    private List<String> imageUrls;
}
