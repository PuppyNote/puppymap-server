package com.puppymapserver.review.service;

import com.puppymapserver.review.entity.Review;

public interface ReviewReadService {
    Review findByIdOrThrow(Long reviewId);
}
