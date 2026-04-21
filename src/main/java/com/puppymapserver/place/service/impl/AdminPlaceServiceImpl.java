package com.puppymapserver.place.service.impl;

import com.puppymapserver.global.exception.NotFoundException;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import com.puppymapserver.place.repository.PlaceRepository;
import com.puppymapserver.place.service.AdminPlaceService;
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
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));
        return PlaceResponse.of(place);
    }

    @Override
    public void approve(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));
        place.approve();
        placeServiceImpl.indexToElasticsearch(place);
    }

    @Override
    public void reject(Long placeId, String reason) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));
        place.reject(reason);
    }
}
