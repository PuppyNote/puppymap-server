package com.puppymapserver.place.service;

import com.puppymapserver.place.service.request.PlaceFilterServiceRequest;
import com.puppymapserver.place.service.request.PlaceSearchServiceRequest;
import com.puppymapserver.place.service.response.PlaceResponse;

import java.util.List;

public interface PlaceReadService {
    List<PlaceResponse> getApprovedPlaces(PlaceFilterServiceRequest request);
    PlaceResponse getApprovedPlace(Long placeId);
    List<PlaceResponse> searchPlaces(PlaceSearchServiceRequest request);
    List<PlaceResponse> getMyPlaces(Long userId);
}
