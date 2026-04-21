package com.puppymapserver.favorite.controller;

import com.puppymapserver.favorite.service.FavoriteService;
import com.puppymapserver.favorite.service.response.FavoriteResponse;
import com.puppymapserver.global.ApiResponse;
import com.puppymapserver.global.security.SecurityService;
import com.puppymapserver.jwt.dto.LoginUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final SecurityService securityService;

    @GetMapping("/users/me/favorites")
    public ApiResponse<List<FavoriteResponse>> getMyFavorites() {
        LoginUserInfo userInfo = securityService.getCurrentLoginUserInfo();
        return ApiResponse.of(HttpStatus.OK, "즐겨찾기 목록 조회 성공",
                favoriteService.getMyFavorites(userInfo.getUserId()));
    }

    @PostMapping("/places/{placeId}/favorites")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> add(@PathVariable Long placeId) {
        LoginUserInfo userInfo = securityService.getCurrentLoginUserInfo();
        favoriteService.add(placeId, userInfo.getUserId());
        return ApiResponse.of(HttpStatus.CREATED, "즐겨찾기 추가 성공", null);
    }

    @DeleteMapping("/places/{placeId}/favorites")
    public ApiResponse<Void> remove(@PathVariable Long placeId) {
        LoginUserInfo userInfo = securityService.getCurrentLoginUserInfo();
        favoriteService.remove(placeId, userInfo.getUserId());
        return ApiResponse.of(HttpStatus.OK, "즐겨찾기 삭제 성공", null);
    }
}
