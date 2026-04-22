package com.puppymapserver.like.service.impl;

import com.puppymapserver.global.exception.NotFoundException;
import com.puppymapserver.global.exception.PuppyMapException;
import com.puppymapserver.like.entity.PlaceLike;
import com.puppymapserver.like.repository.PlaceLikeJpaRepository;
import com.puppymapserver.like.service.PlaceLikeService;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.user.users.entity.User;
import com.puppymapserver.user.users.service.UserReadService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceLikeServiceImpl implements PlaceLikeService {

    private final PlaceLikeJpaRepository placeLikeJpaRepository;
    private final PlaceReadService placeReadService;
    private final UserReadService userReadService;

    @Override
    public void like(Long placeId, Long userId) {
        if (placeLikeJpaRepository.findByPlaceIdAndUserId(placeId, userId).isPresent()) {
            throw new PuppyMapException("이미 좋아요를 누른 장소입니다.");
        }

        Place place = placeReadService.findApprovedByIdOrThrow(placeId);
        User user = userReadService.findById(userId);

        placeLikeJpaRepository.save(PlaceLike.builder().place(place).user(user).build());
    }

    @Override
    public void unlike(Long placeId, Long userId) {
        PlaceLike like = placeLikeJpaRepository.findByPlaceIdAndUserId(placeId, userId)
                .orElseThrow(() -> new NotFoundException("좋아요를 찾을 수 없습니다."));
        placeLikeJpaRepository.delete(like);
    }
}
