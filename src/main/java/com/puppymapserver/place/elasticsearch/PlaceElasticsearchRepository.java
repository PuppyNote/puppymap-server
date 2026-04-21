package com.puppymapserver.place.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PlaceElasticsearchRepository extends ElasticsearchRepository<PlaceDocument, String> {
}
