package com.puppymapserver.review.service.impl;

import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.review.entity.Review;
import com.puppymapserver.review.repository.ReviewJpaRepository;
import com.puppymapserver.review.service.ReviewReadService;
import com.puppymapserver.review.service.ReviewService;
import com.puppymapserver.review.service.request.ReviewCreateServiceRequest;
import com.puppymapserver.review.service.request.ReviewUpdateServiceRequest;
import com.puppymapserver.review.service.response.ReviewResponse;
import com.puppymapserver.user.users.entity.User;
import com.puppymapserver.user.users.service.UserReadService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewJpaRepository reviewJpaRepository;
    private final ReviewReadService reviewReadService;
    private final PlaceReadService placeReadService;
    private final UserReadService userReadService;

    @Override
    public ReviewResponse create(ReviewCreateServiceRequest request) {
        Place place = placeReadService.findApprovedByIdOrThrow(request.getPlaceId());
        User user = userReadService.findById(request.getUserId());

        return ReviewResponse.of(reviewJpaRepository.save(request.toEntity(place, user)));
    }

    @Override
    public ReviewResponse update(ReviewUpdateServiceRequest request) {
        Review review = reviewReadService.findByIdOrThrow(request.getReviewId());

        review.validateOwner(request.getUserId());
        review.update(request.getRating(), request.getComment());
        return ReviewResponse.of(review);
    }

    @Override
    public void delete(Long reviewId, Long userId) {
        Review review = reviewReadService.findByIdOrThrow(reviewId);

        review.validateOwner(userId);
        reviewJpaRepository.delete(review);
    }

    @Override
    public List<ReviewResponse> getByPlace(Long placeId) {
        return reviewJpaRepository.findAllByPlaceId(placeId).stream()
                .map(ReviewResponse::of)
                .toList();
    }
}
