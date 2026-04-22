package com.puppymapserver.place.controller.request;

import com.puppymapserver.place.service.request.PlaceFilterServiceRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlaceFilterRequest {

    private String category;
    private Boolean largeDog;
    private Boolean parking;
    private Boolean offLeash;

    public PlaceFilterServiceRequest toServiceRequest() {
        return PlaceFilterServiceRequest.builder()
                .category(category)
                .largeDog(largeDog)
                .parking(parking)
                .offLeash(offLeash)
                .build();
    }
}
