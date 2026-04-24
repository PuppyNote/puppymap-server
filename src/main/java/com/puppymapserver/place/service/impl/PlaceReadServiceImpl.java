package com.puppymapserver.place.service.impl;

import com.puppymapserver.global.exception.NotFoundException;
import com.puppymapserver.global.page.request.PageInfoServiceRequest;
import com.puppymapserver.global.page.response.PageCustom;
import com.puppymapserver.global.page.response.PageableCustom;
import com.puppymapserver.place.elasticsearch.PlaceElasticsearchService;
import com.puppymapserver.place.elasticsearch.PlaceElasticsearchService.ElasticPageResult;
import com.puppymapserver.redis.service.PlaceLikeRedisService;
import com.puppymapserver.storage.service.S3StorageService;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.repository.PlaceRepository;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.place.service.request.PlaceFilterServiceRequest;
import com.puppymapserver.place.service.request.PlaceSearchServiceRequest;
import com.puppymapserver.place.service.response.PlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceReadServiceImpl implements PlaceReadService {

    private final PlaceRepository placeRepository;
    private final PlaceElasticsearchService placeElasticsearchService;
    private final S3StorageService s3StorageService;
    private final PlaceLikeRedisService placeLikeRedisService;

    @Override
    public List<PlaceResponse> getApprovedPlaces(PlaceFilterServiceRequest request) {
        List<Place> places = placeRepository.findAllApproved().stream()
                .filter(p -> request.getCategory() == null || p.getCategory() == PlaceCategory.valueOf(request.getCategory()))
                .filter(p -> request.getLargeDog() == null || request.getLargeDog().equals(p.getLargeDogAvailable()))
                .filter(p -> request.getParking() == null || request.getParking().equals(p.getParkingAvailable()))
                .filter(p -> request.getOffLeash() == null || request.getOffLeash().equals(p.getOffLeashAvailable()))
                .toList();
        return toResponses(places);
    }

    @Override
    public Place findByIdOrThrow(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));
    }

    @Override
    public Place findApprovedByIdOrThrow(Long placeId) {
        return placeRepository.findApprovedById(placeId)
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));
    }

    @Override
    public PlaceResponse getApprovedPlace(Long placeId) {
        Place place = findApprovedByIdOrThrow(placeId);
        long likeCount = placeLikeRedisService.getLikeCount(placeId)
                .orElse(place.getLikeCount());
        return PlaceResponse.of(place, s3StorageService::getPlaceCloudFrontUrl, likeCount);
    }

    @Override
    public PageCustom<PlaceResponse> searchPlaces(PlaceSearchServiceRequest request, PageInfoServiceRequest pageInfo) {
        int from = (pageInfo.getPage() - 1) * pageInfo.getSize();
        PlaceElasticsearchService.ElasticPageResult result = placeElasticsearchService.searchByGeo(request, from, pageInfo.getSize());

        List<Place> places = result.placeIds().stream()
                .map(id -> placeRepository.findApprovedById(id).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        int totalPage = (int) Math.ceil((double) result.totalElement() / pageInfo.getSize());

        return PageCustom.<PlaceResponse>builder()
                .content(toResponses(places))
                .pageInfo(PageableCustom.builder()
                        .currentPage(pageInfo.getPage())
                        .totalPage(totalPage)
                        .totalElement(result.totalElement())
                        .build())
                .build();
    }

    @Override
    public PageCustom<PlaceResponse> getNearbyByLikeCount(double lat, double lng, double radiusKm, String category, PageInfoServiceRequest pageInfo) {
        int offset = (pageInfo.getPage() - 1) * pageInfo.getSize();
        List<Place> places = placeRepository.findNearbyOrderByLikeCount(lat, lng, radiusKm, category, pageInfo.getSize(), offset);
        long totalElement = placeRepository.countNearby(lat, lng, radiusKm, category);
        int totalPage = (int) Math.ceil((double) totalElement / pageInfo.getSize());

        return PageCustom.<PlaceResponse>builder()
                .content(toResponses(places))
                .pageInfo(PageableCustom.builder()
                        .currentPage(pageInfo.getPage())
                        .totalPage(totalPage)
                        .totalElement(totalElement)
                        .build())
                .build();
    }

    @Override
    public List<PlaceResponse> getMyPlaces(Long userId) {
        return toResponses(placeRepository.findAllByUserId(userId));
    }

    @Override
    public PageCustom<PlaceResponse> getPlacesByKeyword(String keyword, String category, PageInfoServiceRequest pageInfo) {
        int from = (pageInfo.getPage() - 1) * pageInfo.getSize();
        ElasticPageResult result = placeElasticsearchService.searchByKeyword(keyword, category, from, pageInfo.getSize());

        List<Place> places = result.placeIds().stream()
                .map(id -> placeRepository.findApprovedById(id).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        int totalPage = (int) Math.ceil((double) result.totalElement() / pageInfo.getSize());

        return PageCustom.<PlaceResponse>builder()
                .content(toResponses(places))
                .pageInfo(PageableCustom.builder()
                        .currentPage(pageInfo.getPage())
                        .totalPage(totalPage)
                        .totalElement(result.totalElement())
                        .build())
                .build();
    }

    private List<PlaceResponse> toResponses(List<Place> places) {
        List<Long> ids = places.stream().map(Place::getId).toList();
        Map<Long, Long> redisLikeCounts = placeLikeRedisService.getLikeCountBatch(ids);
        return places.stream()
                .map(p -> PlaceResponse.of(
                        p,
                        s3StorageService::getPlaceCloudFrontUrl,
                        redisLikeCounts.getOrDefault(p.getId(), p.getLikeCount())
                ))
                .toList();
    }
}
