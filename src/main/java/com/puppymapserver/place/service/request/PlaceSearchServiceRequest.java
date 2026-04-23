package com.puppymapserver.place.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlaceSearchServiceRequest {

    private final String keyword;
    private final Double lat;
    private final Double lng;
    private final Double radiusKm;
    private final String category;

    @Builder
    private PlaceSearchServiceRequest(String keyword, Double lat, Double lng, Double radiusKm, String category) {
        this.keyword = keyword;
        this.lat = lat;
        this.lng = lng;
        this.radiusKm = radiusKm;
        this.category = category;
    }
}
