package com.puppymapserver.place.controller.request;

import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.service.request.PlaceCreateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class PlaceCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private PlaceCategory category;

    private Boolean largeDogAvailable;
    private Boolean parkingAvailable;
    private Boolean offLeashAvailable;
    private List<String> imageUrls;

    public PlaceCreateServiceRequest toServiceRequest() {
        return PlaceCreateServiceRequest.builder()
                .title(title)
                .content(content)
                .latitude(latitude)
                .longitude(longitude)
                .category(category)
                .largeDogAvailable(largeDogAvailable)
                .parkingAvailable(parkingAvailable)
                .offLeashAvailable(offLeashAvailable)
                .imageUrls(imageUrls)
                .build();
    }
}
