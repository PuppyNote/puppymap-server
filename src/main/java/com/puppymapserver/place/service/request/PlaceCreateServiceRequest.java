package com.puppymapserver.place.service.request;

import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.PlaceImage;
import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.user.users.entity.User;
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

    public Place toEntity(User user) {
        Place place = Place.builder()
                .user(user)
                .title(title)
                .content(content)
                .latitude(latitude)
                .longitude(longitude)
                .category(category)
                .largeDogAvailable(largeDogAvailable)
                .parkingAvailable(parkingAvailable)
                .offLeashAvailable(offLeashAvailable)
                .build();

        if (imageUrls != null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                place.getImages().add(PlaceImage.builder()
                        .place(place)
                        .imageUrl(imageUrls.get(i))
                        .sortOrder(i)
                        .build());
            }
        }

        return place;
    }
}
