package com.puppymapserver.review.service.request;

import com.puppymapserver.place.entity.Place;
import com.puppymapserver.review.entity.Review;
import com.puppymapserver.user.users.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewCreateServiceRequest {
    private Long placeId;
    private Long userId;
    private Integer rating;
    private String comment;

    public Review toEntity(Place place, User user) {
        return Review.builder()
                .place(place)
                .user(user)
                .rating(rating)
                .comment(comment)
                .build();
    }
}
