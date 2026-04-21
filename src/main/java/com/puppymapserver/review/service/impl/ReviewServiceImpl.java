package com.puppymapserver.review.service.impl;

import com.puppymapserver.global.exception.NotFoundException;
import com.puppymapserver.global.exception.PuppyMapException;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.repository.PlaceRepository;
import com.puppymapserver.review.entity.Review;
import com.puppymapserver.review.repository.ReviewJpaRepository;
import com.puppymapserver.review.service.ReviewService;
import com.puppymapserver.review.service.request.ReviewCreateServiceRequest;
import com.puppymapserver.review.service.request.ReviewUpdateServiceRequest;
import com.puppymapserver.review.service.response.ReviewResponse;
import com.puppymapserver.user.users.entity.User;
import com.puppymapserver.user.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewJpaRepository reviewJpaRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewResponse create(ReviewCreateServiceRequest request) {
        Place place = placeRepository.findApprovedById(request.getPlaceId())
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        Review review = Review.builder()
                .place(place)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return ReviewResponse.of(reviewJpaRepository.save(review));
    }

    @Override
    public ReviewResponse update(ReviewUpdateServiceRequest request) {
        Review review = reviewJpaRepository.findById(request.getReviewId())
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(request.getUserId())) {
            throw new PuppyMapException("본인의 리뷰만 수정할 수 있습니다.");
        }

        review.update(request.getRating(), request.getComment());
        return ReviewResponse.of(review);
    }

    @Override
    public void delete(Long reviewId, Long userId) {
        Review review = reviewJpaRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(userId)) {
            throw new PuppyMapException("본인의 리뷰만 삭제할 수 있습니다.");
        }

        reviewJpaRepository.delete(review);
    }

    @Override
    public List<ReviewResponse> getByPlace(Long placeId) {
        return reviewJpaRepository.findAllByPlaceId(placeId).stream()
                .map(ReviewResponse::of)
                .toList();
    }
}
