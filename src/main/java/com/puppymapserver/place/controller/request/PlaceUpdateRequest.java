package com.puppymapserver.place.controller.request;

import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.service.request.PlaceUpdateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PlaceUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private PlaceCategory category;

    private Boolean largeDogAvailable;
    private Boolean parkingAvailable;
    private Boolean offLeashAvailable;

    public PlaceUpdateServiceRequest toServiceRequest(Long placeId, Long userId) {
        return PlaceUpdateServiceRequest.builder()
                .placeId(placeId)
                .userId(userId)
                .title(title)
                .content(content)
                .category(category)
                .largeDogAvailable(largeDogAvailable)
                .parkingAvailable(parkingAvailable)
                .offLeashAvailable(offLeashAvailable)
                .build();
    }
}
