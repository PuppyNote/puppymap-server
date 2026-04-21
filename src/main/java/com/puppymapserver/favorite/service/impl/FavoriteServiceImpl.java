package com.puppymapserver.favorite.service.impl;

import com.puppymapserver.favorite.entity.Favorite;
import com.puppymapserver.favorite.repository.FavoriteJpaRepository;
import com.puppymapserver.favorite.service.FavoriteService;
import com.puppymapserver.favorite.service.response.FavoriteResponse;
import com.puppymapserver.global.exception.NotFoundException;
import com.puppymapserver.global.exception.PuppyMapException;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.repository.PlaceRepository;
import com.puppymapserver.user.users.entity.User;
import com.puppymapserver.user.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteJpaRepository favoriteJpaRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    @Override
    public void add(Long placeId, Long userId) {
        if (favoriteJpaRepository.findByPlaceIdAndUserId(placeId, userId).isPresent()) {
            throw new PuppyMapException("이미 즐겨찾기에 추가된 장소입니다.");
        }

        Place place = placeRepository.findApprovedById(placeId)
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        favoriteJpaRepository.save(Favorite.builder().place(place).user(user).build());
    }

    @Override
    public void remove(Long placeId, Long userId) {
        Favorite favorite = favoriteJpaRepository.findByPlaceIdAndUserId(placeId, userId)
                .orElseThrow(() -> new NotFoundException("즐겨찾기를 찾을 수 없습니다."));
        favoriteJpaRepository.delete(favorite);
    }

    @Override
    public List<FavoriteResponse> getMyFavorites(Long userId) {
        return favoriteJpaRepository.findAllByUserId(userId).stream()
                .map(FavoriteResponse::of)
                .toList();
    }
}
