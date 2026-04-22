package com.puppymapserver.place.controller.request;

import com.puppymapserver.place.service.request.PlaceSearchServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlaceSearchRequest {

    private String keyword;

    @NotNull(message = "위도는 필수입니다.")
    private Double lat;

    @NotNull(message = "경도는 필수입니다.")
    private Double lng;

    @NotNull(message = "반경은 필수입니다.")
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
