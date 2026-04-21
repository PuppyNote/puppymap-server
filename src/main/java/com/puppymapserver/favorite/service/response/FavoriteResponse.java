package com.puppymapserver.favorite.service.response;

import com.puppymapserver.favorite.entity.Favorite;
import com.puppymapserver.place.service.response.PlaceResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteResponse {
    private Long favoriteId;
    private PlaceResponse place;

    public static FavoriteResponse of(Favorite favorite) {
        return FavoriteResponse.builder()
                .favoriteId(favorite.getId())
                .place(PlaceResponse.of(favorite.getPlace()))
                .build();
    }
}
