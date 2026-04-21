package com.puppymapserver.place.service;

import com.puppymapserver.place.service.response.PlaceResponse;

import java.util.List;

public interface AdminPlaceService {
    List<PlaceResponse> getAllPlaces(String status);
    PlaceResponse getPlace(Long placeId);
    void approve(Long placeId);
    void reject(Long placeId, String reason);
}
