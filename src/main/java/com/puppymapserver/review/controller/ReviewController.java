package com.puppymapserver.review.controller;

import com.puppymapserver.global.ApiResponse;
import com.puppymapserver.global.security.SecurityService;
import com.puppymapserver.jwt.dto.LoginUserInfo;
import com.puppymapserver.review.controller.request.ReviewUpdateRequest;
import com.puppymapserver.review.service.ReviewService;
import com.puppymapserver.review.service.request.ReviewUpdateServiceRequest;
import com.puppymapserver.review.service.response.ReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityService securityService;

    @PatchMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> update(@PathVariable Long reviewId,
                                               @Valid @RequestBody ReviewUpdateRequest request) {
        LoginUserInfo userInfo = securityService.getCurrentLoginUserInfo();
        return ApiResponse.of(HttpStatus.OK, "리뷰 수정 성공",
                reviewService.update(ReviewUpdateServiceRequest.builder()
                        .reviewId(reviewId)
                        .userId(userInfo.getUserId())
                        .rating(request.getRating())
                        .comment(request.getComment())
                        .build()));
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> delete(@PathVariable Long reviewId) {
        LoginUserInfo userInfo = securityService.getCurrentLoginUserInfo();
        reviewService.delete(reviewId, userInfo.getUserId());
        return ApiResponse.of(HttpStatus.OK, "리뷰 삭제 성공", null);
    }
}
