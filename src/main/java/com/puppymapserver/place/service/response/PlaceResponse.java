package com.puppymapserver.place.service.response;

import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.PlaceImage;
import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Getter
@Builder
public class PlaceResponse {
    private Long id;
    private Long userId;
    private String userNickName;
    private String title;
    private String content;
    private Double latitude;
    private Double longitude;
    private PlaceCategory category;
    private PlaceStatus status;
    private Boolean largeDogAvailable;
    private Boolean parkingAvailable;
    private Boolean offLeashAvailable;
    private List<String> imageUrls;
    private long likeCount;
    private LocalDateTime createdDate;

    public static PlaceResponse of(Place place, Function<String, String> imageUrlMapper) {
        return of(place, imageUrlMapper, place.getLikeCount());
    }

    public static PlaceResponse of(Place place, Function<String, String> imageUrlMapper, long likeCount) {
        return PlaceResponse.builder()
                .id(place.getId())
                .userId(place.getUser().getId())
                .userNickName(place.getUser().getNickName())
                .title(place.getTitle())
                .content(place.getContent())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .category(place.getCategory())
                .status(place.getStatus())
                .largeDogAvailable(place.getLargeDogAvailable())
                .parkingAvailable(place.getParkingAvailable())
                .offLeashAvailable(place.getOffLeashAvailable())
                .imageUrls(place.getImages().stream()
                        .map(PlaceImage::getImageUrl)
                        .map(imageUrlMapper)
                        .toList())
                .likeCount(likeCount)
                .createdDate(place.getCreatedDate())
                .build();
    }
}
