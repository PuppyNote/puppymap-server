package com.puppymapserver.favorite.service.response;

import com.puppymapserver.favorite.entity.Favorite;
import com.puppymapserver.place.service.response.PlaceResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Function;

@Getter
@Builder
public class FavoriteResponse {
    private Long favoriteId;
    private PlaceResponse place;

    public static FavoriteResponse of(Favorite favorite, Function<String, String> imageUrlMapper) {
        return FavoriteResponse.builder()
                .favoriteId(favorite.getId())
                .place(PlaceResponse.of(favorite.getPlace(), imageUrlMapper))
                .build();
    }
}
