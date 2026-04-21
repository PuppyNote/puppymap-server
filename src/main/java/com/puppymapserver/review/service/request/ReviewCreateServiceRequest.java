package com.puppymapserver.review.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewCreateServiceRequest {
    private Long placeId;
    private Long userId;
    private Integer rating;
    private String comment;
}
