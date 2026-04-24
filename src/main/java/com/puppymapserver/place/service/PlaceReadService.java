package com.puppymapserver.place.service;

import com.puppymapserver.global.page.request.PageInfoServiceRequest;
import com.puppymapserver.global.page.response.PageCustom;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.service.request.PlaceFilterServiceRequest;
import com.puppymapserver.place.service.request.PlaceSearchServiceRequest;
import com.puppymapserver.place.service.response.PlaceResponse;

import java.util.List;

public interface PlaceReadService {
    Place findByIdOrThrow(Long placeId);
    Place findApprovedByIdOrThrow(Long placeId);
    List<PlaceResponse> getApprovedPlaces(PlaceFilterServiceRequest request);
    PlaceResponse getApprovedPlace(Long placeId);
    List<PlaceResponse> searchPlaces(PlaceSearchServiceRequest request);
    List<PlaceResponse> getTop20NearbyByLikeCount(double lat, double lng, double radiusKm, String category);
    List<PlaceResponse> getMyPlaces(Long userId);
    PageCustom<PlaceResponse> getPlacesByKeyword(String keyword, PageInfoServiceRequest pageInfo);
}
