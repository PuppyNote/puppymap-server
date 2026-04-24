package com.puppymapserver.place.elasticsearch;

import com.puppymapserver.place.service.request.PlaceSearchServiceRequest;

import java.util.List;

public interface PlaceElasticsearchService {

    ElasticPageResult searchByGeo(PlaceSearchServiceRequest request, int from, int size);

    ElasticPageResult searchByKeyword(String keyword, String category, int from, int size);

    record ElasticPageResult(List<Long> placeIds, long totalElement) {}
}
