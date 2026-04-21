package com.puppymapserver.place.controller;

import com.puppymapserver.global.ApiResponse;
import com.puppymapserver.place.service.AdminPlaceService;
import com.puppymapserver.place.service.response.PlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/places")
@RequiredArgsConstructor
public class AdminPlaceController {

    private final AdminPlaceService adminPlaceService;

    @GetMapping
    public ApiResponse<List<PlaceResponse>> getPlaces(
            @RequestParam(required = false) String status) {
        return ApiResponse.of(HttpStatus.OK, "제보 목록 조회 성공", adminPlaceService.getAllPlaces(status));
    }

    @GetMapping("/{placeId}")
    public ApiResponse<PlaceResponse> getPlace(@PathVariable Long placeId) {
        return ApiResponse.of(HttpStatus.OK, "제보 조회 성공", adminPlaceService.getPlace(placeId));
    }

    @PatchMapping("/{placeId}/approve")
    public ApiResponse<Void> approve(@PathVariable Long placeId) {
        adminPlaceService.approve(placeId);
        return ApiResponse.of(HttpStatus.OK, "장소 승인 완료", null);
    }

    @PatchMapping("/{placeId}/reject")
    public ApiResponse<Void> reject(@PathVariable Long placeId,
                                    @RequestParam String reason) {
        adminPlaceService.reject(placeId, reason);
        return ApiResponse.of(HttpStatus.OK, "장소 거절 완료", null);
    }
}
