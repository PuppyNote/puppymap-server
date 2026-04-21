package com.puppymapserver.review.service;

import com.puppymapserver.review.service.request.ReviewCreateServiceRequest;
import com.puppymapserver.review.service.request.ReviewUpdateServiceRequest;
import com.puppymapserver.review.service.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse create(ReviewCreateServiceRequest request);
    ReviewResponse update(ReviewUpdateServiceRequest request);
    void delete(Long reviewId, Long userId);
    List<ReviewResponse> getByPlace(Long placeId);
}
