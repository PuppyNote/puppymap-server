package com.puppymapserver.place.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlaceFilterServiceRequest {

    private final String category;
    private final Boolean largeDog;
    private final Boolean parking;
    private final Boolean offLeash;

    @Builder
    private PlaceFilterServiceRequest(String category, Boolean largeDog, Boolean parking, Boolean offLeash) {
        this.category = category;
        this.largeDog = largeDog;
        this.parking = parking;
        this.offLeash = offLeash;
    }
}
