package com.puppymapserver.place.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.puppymapserver.global.exception.NotFoundException;
import com.puppymapserver.place.elasticsearch.PlaceDocument;
import com.puppymapserver.storage.service.S3StorageService;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import com.puppymapserver.place.repository.PlaceRepository;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.place.service.request.PlaceFilterServiceRequest;
import com.puppymapserver.place.service.request.PlaceSearchServiceRequest;
import com.puppymapserver.place.service.response.PlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceReadServiceImpl implements PlaceReadService {

    private final PlaceRepository placeRepository;
    private final ElasticsearchClient elasticsearchClient;
    private final S3StorageService s3StorageService;

    @Override
    public List<PlaceResponse> getApprovedPlaces(PlaceFilterServiceRequest request) {
        return placeRepository.findAllApproved().stream()
                .filter(p -> request.getCategory() == null || p.getCategory() == PlaceCategory.valueOf(request.getCategory()))
                .filter(p -> request.getLargeDog() == null || request.getLargeDog().equals(p.getLargeDogAvailable()))
                .filter(p -> request.getParking() == null || request.getParking().equals(p.getParkingAvailable()))
                .filter(p -> request.getOffLeash() == null || request.getOffLeash().equals(p.getOffLeashAvailable()))
                .map(p -> PlaceResponse.of(p, s3StorageService::getPlaceCloudFrontUrl))
                .toList();
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
        return PlaceResponse.of(findApprovedByIdOrThrow(placeId), s3StorageService::getPlaceCloudFrontUrl);
    }

    @Override
    public List<PlaceResponse> searchPlaces(PlaceSearchServiceRequest request) {
        try {
            List<Query> queries = new ArrayList<>();

            // 지도 중심 반경 필터 (항상 적용)
            String distance = request.getRadiusKm() + "km";
            queries.add(Query.of(q -> q.geoDistance(g -> g
                    .field("location")
                    .location(l -> l.latlon(ll -> ll.lat(request.getLat()).lon(request.getLng())))
                    .distance(distance))));

            // 키워드 필터 (선택)
            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                queries.add(Query.of(q -> q.multiMatch(m -> m
                        .query(request.getKeyword())
                        .fields("title", "content"))));
            }

            BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
            queries.forEach(boolBuilder::must);
            Query finalQuery = Query.of(q -> q.bool(boolBuilder.build()));

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("places")
                    .query(finalQuery));

            SearchResponse<PlaceDocument> response = elasticsearchClient.search(searchRequest, PlaceDocument.class);

            List<Long> placeIds = response.hits().hits().stream()
                    .map(Hit::id).filter(Objects::nonNull)
                    .map(Long::valueOf)
                    .toList();

            return placeIds.stream()
                    .map(id -> placeRepository.findApprovedById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .map(p -> PlaceResponse.of(p, s3StorageService::getPlaceCloudFrontUrl))
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("검색 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<PlaceResponse> getTop20NearbyByLikeCount(double lat, double lng, double radiusKm) {
        return placeRepository.findTop20NearbyOrderByLikeCount(lat, lng, radiusKm).stream()
                .map(p -> PlaceResponse.of(p, s3StorageService::getPlaceCloudFrontUrl))
                .toList();
    }

    @Override
    public List<PlaceResponse> getMyPlaces(Long userId) {
        return placeRepository.findAllByUserId(userId).stream()
                .map(p -> PlaceResponse.of(p, s3StorageService::getPlaceCloudFrontUrl))
                .toList();
    }
}
