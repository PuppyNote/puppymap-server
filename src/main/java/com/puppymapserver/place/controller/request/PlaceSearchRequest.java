package com.puppymapserver.place.controller.request;

import com.puppymapserver.place.service.request.PlaceSearchServiceRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlaceSearchRequest {

    private String keyword;
    private Double lat;
    private Double lng;
    private Double radius;

    public PlaceSearchServiceRequest toServiceRequest() {
        return PlaceSearchServiceRequest.builder()
                .keyword(keyword)
                .lat(lat)
                .lng(lng)
                .radiusKm(radius)
                .build();
    }
}
