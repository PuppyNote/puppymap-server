package com.puppymapserver.place.service;

import com.puppymapserver.place.service.response.PlaceResponse;

import java.util.List;

public interface PlaceReadService {
    List<PlaceResponse> getApprovedPlaces(String category, Boolean largeDog, Boolean parking, Boolean offLeash);
    PlaceResponse getApprovedPlace(Long placeId);
    List<PlaceResponse> searchPlaces(String keyword, Double lat, Double lng, Double radiusKm);
    List<PlaceResponse> getMyPlaces(Long userId);
}
