package com.puppymapserver.place.repository;

import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.enums.PlaceStatus;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository {
    Place save(Place place);
    Optional<Place> findById(Long id);
    Optional<Place> findApprovedById(Long id);
    List<Place> findAllApproved();
    List<Place> findAllByUserId(Long userId);
    List<Place> findAllByStatus(PlaceStatus status);
    List<Place> findTop20NearbyOrderByLikeCount(double lat, double lng, double radiusKm);
    void delete(Place place);
    void updateLikeCount(Long placeId);
}
