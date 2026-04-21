package com.puppymapserver.review.service.response;

import com.puppymapserver.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
    private Long id;
    private Long placeId;
    private Long userId;
    private String userNickName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdDate;

    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .placeId(review.getPlace().getId())
                .userId(review.getUser().getId())
                .userNickName(review.getUser().getNickName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdDate(review.getCreatedDate())
                .build();
    }
}
