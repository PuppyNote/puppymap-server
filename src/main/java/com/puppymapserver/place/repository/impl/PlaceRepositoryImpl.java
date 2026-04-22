package com.puppymapserver.place.repository.impl;

import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import com.puppymapserver.place.repository.PlaceJpaRepository;
import com.puppymapserver.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepository {

    private final PlaceJpaRepository placeJpaRepository;

    @Override
    public Place save(Place place) {
        return placeJpaRepository.save(place);
    }

    @Override
    public Optional<Place> findById(Long id) {
        return placeJpaRepository.findById(id);
    }

    @Override
    public Optional<Place> findApprovedById(Long id) {
        return placeJpaRepository.findApprovedById(id);
    }

    @Override
    public List<Place> findAllApproved() {
        return placeJpaRepository.findAllApproved();
    }

    @Override
    public List<Place> findAllByUserId(Long userId) {
        return placeJpaRepository.findAllByUserId(userId);
    }

    @Override
    public List<Place> findAllByStatus(PlaceStatus status) {
        return placeJpaRepository.findAllByStatus(status);
    }

    @Override
    public void delete(Place place) {
        placeJpaRepository.delete(place);
    }

    @Override
    public void updateLikeCount(Long placeId) {
        placeJpaRepository.updateLikeCount(placeId);
    }
}
