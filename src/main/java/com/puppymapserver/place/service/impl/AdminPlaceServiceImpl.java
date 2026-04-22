package com.puppymapserver.place.service.impl;

import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import com.puppymapserver.place.repository.PlaceRepository;
import com.puppymapserver.place.service.AdminPlaceService;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.place.service.response.PlaceResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminPlaceServiceImpl implements AdminPlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceReadService placeReadService;
    private final PlaceServiceImpl placeServiceImpl;

    @Override
    public List<PlaceResponse> getAllPlaces(String status) {
        if (status == null) {
            return placeRepository.findAllByStatus(PlaceStatus.PENDING).stream()
                    .map(PlaceResponse::of)
                    .toList();
        }
        return placeRepository.findAllByStatus(PlaceStatus.valueOf(status)).stream()
                .map(PlaceResponse::of)
                .toList();
    }

    @Override
    public PlaceResponse getPlace(Long placeId) {
        return PlaceResponse.of(placeReadService.findByIdOrThrow(placeId));
    }

    @Override
    public void approve(Long placeId) {
        Place place = placeReadService.findByIdOrThrow(placeId);
        place.approve();
        placeServiceImpl.indexToElasticsearch(place);
    }

    @Override
    public void reject(Long placeId, String reason) {
        placeReadService.findByIdOrThrow(placeId).reject(reason);
    }
}
