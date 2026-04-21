package com.puppymapserver.place.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.puppymapserver.global.exception.NotFoundException;
import com.puppymapserver.place.elasticsearch.PlaceDocument;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import com.puppymapserver.place.repository.PlaceRepository;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.place.service.response.PlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceReadServiceImpl implements PlaceReadService {

    private final PlaceRepository placeRepository;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public List<PlaceResponse> getApprovedPlaces(String category, Boolean largeDog, Boolean parking, Boolean offLeash) {
        return placeRepository.findAllApproved().stream()
                .filter(p -> category == null || p.getCategory() == PlaceCategory.valueOf(category))
                .filter(p -> largeDog == null || largeDog.equals(p.getLargeDogAvailable()))
                .filter(p -> parking == null || parking.equals(p.getParkingAvailable()))
                .filter(p -> offLeash == null || offLeash.equals(p.getOffLeashAvailable()))
                .map(PlaceResponse::of)
                .toList();
    }

    @Override
    public PlaceResponse getApprovedPlace(Long placeId) {
        Place place = placeRepository.findApprovedById(placeId)
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));
        return PlaceResponse.from(place);
    }

    @Override
    public List<PlaceResponse> searchPlaces(String keyword, Double lat, Double lng, Double radiusKm) {
        try {
            List<Query> queries = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                queries.add(Query.of(q -> q.multiMatch(m -> m
                        .query(keyword)
                        .fields("title", "content"))));
            }

            if (lat != null && lng != null && radiusKm != null) {
                String distance = radiusKm + "km";
                queries.add(Query.of(q -> q.geoDistance(g -> g
                        .field("location")
                        .location(l -> l.latlon(ll -> ll.lat(lat).lon(lng)))
                        .distance(distance))));
            }

            BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
            queries.forEach(boolBuilder::must);
            Query finalQuery = Query.of(q -> q.bool(boolBuilder.build()));

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("places")
                    .query(finalQuery));

            SearchResponse<PlaceDocument> response = elasticsearchClient.search(searchRequest, PlaceDocument.class);

            List<Long> placeIds = response.hits().hits().stream()
                    .map(Hit::id)
                    .map(Long::valueOf)
                    .toList();

            return placeIds.stream()
                    .map(id -> placeRepository.findApprovedById(id).orElse(null))
                    .filter(p -> p != null)
                    .map(PlaceResponse::of)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("검색 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<PlaceResponse> getMyPlaces(Long userId) {
        return placeRepository.findAllByUserId(userId).stream()
                .map(PlaceResponse::of)
                .toList();
    }
}
