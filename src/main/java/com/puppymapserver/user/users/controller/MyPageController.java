package com.puppymapserver.user.users.controller;

import com.puppymapserver.global.ApiResponse;
import com.puppymapserver.global.security.SecurityService;
import com.puppymapserver.jwt.dto.LoginUserInfo;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.place.service.response.PlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class MyPageController {

    private final PlaceReadService placeReadService;
    private final SecurityService securityService;

    @GetMapping("/places")
    public ApiResponse<List<PlaceResponse>> getMyPlaces() {
        LoginUserInfo userInfo = securityService.getCurrentLoginUserInfo();
        return ApiResponse.of(HttpStatus.OK, "내 제보 목록 조회 성공",
                placeReadService.getMyPlaces(userInfo.getUserId()));
    }
}
