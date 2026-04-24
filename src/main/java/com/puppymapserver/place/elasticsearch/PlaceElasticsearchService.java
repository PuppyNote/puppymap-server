package com.puppymapserver.place.elasticsearch;

import com.puppymapserver.place.service.request.PlaceSearchServiceRequest;

import java.util.List;

public interface PlaceElasticsearchService {

    List<Long> searchByGeo(PlaceSearchServiceRequest request);

    ElasticPageResult searchByKeyword(String keyword, int from, int size);

    record ElasticPageResult(List<Long> placeIds, long totalElement) {}
}
