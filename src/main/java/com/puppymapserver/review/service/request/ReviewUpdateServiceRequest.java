package com.puppymapserver.review.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewUpdateServiceRequest {
    private Long reviewId;
    private Long userId;
    private Integer rating;
    private String comment;
}
