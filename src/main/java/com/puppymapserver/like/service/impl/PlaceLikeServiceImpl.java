package com.puppymapserver.like.service.impl;

import com.puppymapserver.global.security.SecurityService;
import com.puppymapserver.like.repository.PlaceLikeJpaRepository;
import com.puppymapserver.like.service.PlaceLikeService;
import com.puppymapserver.like.service.response.PlaceLikeToggleResponse;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.redis.service.PlaceLikeRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceLikeServiceImpl implements PlaceLikeService {

    private final PlaceLikeJpaRepository placeLikeJpaRepository;
    private final PlaceReadService placeReadService;
    private final PlaceLikeRedisService placeLikeRedisService;
    private final SecurityService securityService;

    @Override
    @Transactional
    public PlaceLikeToggleResponse toggleLike(Long placeId) {
        placeReadService.findApprovedByIdOrThrow(placeId);

        Long userId = securityService.getCurrentLoginUserInfo().getUserId();

        initializeCacheIfAbsent(placeId, userId);

        List<Long> result = placeLikeRedisService.toggle(userId, placeId);
        boolean liked = result.get(0) == 1L;
        long likeCount = result.get(1);
        return PlaceLikeToggleResponse.of(liked, likeCount);
    }

    private void initializeCacheIfAbsent(Long placeId, Long userId) {
        if (!placeLikeRedisService.existsCountCache(placeId)) {
            long count = placeLikeJpaRepository.countByPlaceId(placeId);
            placeLikeRedisService.setCountCache(placeId, count);
        }

        if (!placeLikeRedisService.existsLikedCache(userId, placeId)) {
            boolean liked = placeLikeJpaRepository.findByPlaceIdAndUserId(placeId, userId).isPresent();
            placeLikeRedisService.setLikedCache(userId, placeId, liked);
        }
    }
}
