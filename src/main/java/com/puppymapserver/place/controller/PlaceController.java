package com.puppymapserver.place.controller;

import com.puppymapserver.global.ApiResponse;
import com.puppymapserver.global.page.request.PageInfoRequest;
import com.puppymapserver.global.page.response.PageCustom;
import com.puppymapserver.global.security.SecurityService;
import com.puppymapserver.jwt.dto.LoginUserInfo;
import com.puppymapserver.like.service.PlaceLikeService;
import com.puppymapserver.like.service.response.PlaceLikeToggleResponse;
import com.puppymapserver.place.controller.request.PlaceCreateRequest;
import com.puppymapserver.place.controller.request.PlaceFilterRequest;
import com.puppymapserver.place.controller.request.PlaceSearchRequest;
import com.puppymapserver.place.controller.request.PlaceUpdateRequest;
import com.puppymapserver.place.controller.request.ReviewRequest;
import com.puppymapserver.place.entity.enums.TagType;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.place.service.PlaceService;
import com.puppymapserver.place.service.response.PlaceResponse;
import com.puppymapserver.review.service.ReviewService;
import com.puppymapserver.review.service.request.ReviewCreateServiceRequest;
import com.puppymapserver.review.service.response.ReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final PlaceReadService placeReadService;
    private final ReviewService reviewService;
    private final PlaceLikeService placeLikeService;
    private final SecurityService securityService;

    // Public

    @GetMapping
    public ApiResponse<List<PlaceResponse>> getPlaces(@ModelAttribute PlaceFilterRequest request) {
        return ApiResponse.of(HttpStatus.OK, "장소 목록 조회 성공",
                placeReadService.getApprovedPlaces(request.toServiceRequest()));
    }

    @GetMapping("/{placeId}")
    public ApiResponse<PlaceResponse> getPlace(@PathVariable Long placeId) {
        return ApiResponse.of(HttpStatus.OK, "장소 조회 성공", placeReadService.getApprovedPlace(placeId));
    }

    @GetMapping("/nearby/top")
    public ApiResponse<List<PlaceResponse>> getTop20Nearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5.0") double radiusKm,
            @RequestParam(required = false) String category) {
        return ApiResponse.of(HttpStatus.OK, "근처 인기 장소 조회 성공",
                placeReadService.getTop20NearbyByLikeCount(lat, lng, radiusKm, category));
    }

    @GetMapping("/search")
    public ApiResponse<List<PlaceResponse>> search(@Validated @ModelAttribute PlaceSearchRequest request) {
        return ApiResponse.of(HttpStatus.OK, "검색 성공",
                placeReadService.searchPlaces(request.toServiceRequest()));
    }

    @GetMapping("/list")
    public ApiResponse<PageCustom<PlaceResponse>> getPlacesByKeyword(
            @RequestParam(required = false) String keyword,
            @ModelAttribute PageInfoRequest pageInfo) {
        return ApiResponse.of(HttpStatus.OK, "장소 목록 조회 성공",
                placeReadService.getPlacesByKeyword(keyword, pageInfo.toServiceRequest()));
    }

    @GetMapping("/{placeId}/reviews")
    public ApiResponse<List<ReviewResponse>> getReviews(@PathVariable Long placeId) {
        return ApiResponse.of(HttpStatus.OK, "리뷰 목록 조회 성공", reviewService.getByPlace(placeId));
    }

    // User (로그인 필수)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PlaceResponse> create(@Valid @RequestBody PlaceCreateRequest request) {
        LoginUserInfo userInfo = securityService.getCurrentLoginUserInfo();
        return ApiResponse.of(HttpStatus.CREATED, "장소 제보 성공",
                placeService.create(userInfo.getUserId(), request.toServiceRequest()));
    }

    @PatchMapping("/{placeId}")
    public ApiResponse<PlaceResponse> update(@PathVariable Long placeId,
                                              @Valid @RequestBody PlaceUpdateRequest request) {
        LoginUserInfo userInfo = securityService.getCurrentLoginUserInfo();
        return ApiResponse.of(HttpStatus.OK, "장소 수정 성공",
                placeService.update(request.toServiceRequest(placeId, userInfo.getUserId())));
    }

    @DeleteMapping("/{placeId}")
    public ApiResponse<Void> delete(@PathVariable Long placeId) {
        LoginUserInfo userInfo = securityService.getCurrentLoginUserInfo();
        placeService.delete(placeId, userInfo.getUserId());
        return ApiResponse.of(HttpStatus.OK, "장소 삭제 성공", null);
    }

    @PostMapping("/{placeId}/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> addTag(@PathVariable Long placeId, @RequestParam TagType tagType) {
        LoginUserInfo userInfo = securityService.getCurrentLoginUserInfo();
        placeService.addTag(placeId, userInfo.getUserId(), tagType);
        return ApiResponse.of(HttpStatus.CREATED, "태그 등록 성공", null);
    }

    @PostMapping("/{placeId}/likes")
    public ApiResponse<PlaceLikeToggleResponse> toggleLike(@PathVariable Long placeId) {
        return ApiResponse.of(HttpStatus.OK, "좋아요 토글 성공", placeLikeService.toggleLike(placeId));
    }

    @PostMapping("/{placeId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewResponse> createReview(@PathVariable Long placeId,
                                                     @Valid @RequestBody ReviewRequest reviewRequest) {
        LoginUserInfo userInfo = securityService.getCurrentLoginUserInfo();
        return ApiResponse.of(HttpStatus.CREATED, "리뷰 작성 성공",
                reviewService.create(ReviewCreateServiceRequest.builder()
                        .placeId(placeId)
                        .userId(userInfo.getUserId())
                        .rating(reviewRequest.getRating())
                        .comment(reviewRequest.getComment())
                        .build()));
    }
}
