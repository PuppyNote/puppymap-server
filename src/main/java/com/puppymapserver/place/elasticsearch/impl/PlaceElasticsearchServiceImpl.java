package com.puppymapserver.place.elasticsearch.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.puppymapserver.place.elasticsearch.PlaceDocument;
import com.puppymapserver.place.elasticsearch.PlaceElasticsearchService;
import com.puppymapserver.place.service.request.PlaceSearchServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PlaceElasticsearchServiceImpl implements PlaceElasticsearchService {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public ElasticPageResult searchByGeo(PlaceSearchServiceRequest request, int from, int size) {
        try {
            List<Query> queries = new ArrayList<>();

            queries.add(Query.of(q -> q.geoDistance(g -> g
                    .field("location")
                    .location(l -> l.latlon(ll -> ll.lat(request.getLat()).lon(request.getLng())))
                    .distance(request.getRadiusKm() + "km"))));

            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                queries.add(Query.of(q -> q.multiMatch(m -> m
                        .query(request.getKeyword())
                        .fields("title", "content"))));
            }

            if (request.getCategory() != null && !request.getCategory().isBlank()) {
                String category = request.getCategory();
                queries.add(Query.of(q -> q.term(t -> t.field("category").value(category))));
            }

            BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
            queries.forEach(boolBuilder::must);

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("places")
                    .source(src -> src.fetch(false))
                    .query(Query.of(q -> q.bool(boolBuilder.build())))
                    .from(from)
                    .size(size));

            SearchResponse<PlaceDocument> response = elasticsearchClient.search(searchRequest, PlaceDocument.class);

            long totalElement = response.hits().total() != null ? response.hits().total().value() : 0;
            return new ElasticPageResult(extractIds(response), totalElement);
        } catch (IOException e) {
            throw new RuntimeException("검색 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public ElasticPageResult searchByKeyword(String keyword, int from, int size) {
        try {
            Query query = (keyword != null && !keyword.isBlank())
                    ? Query.of(q -> q.multiMatch(m -> m.query(keyword).fields("title", "content")))
                    : Query.of(q -> q.matchAll(m -> m));

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("places")
                    .source(src -> src.fetch(false))
                    .query(query)
                    .from(from)
                    .size(size));

            SearchResponse<PlaceDocument> response = elasticsearchClient.search(searchRequest, PlaceDocument.class);

            long totalElement = response.hits().total() != null ? response.hits().total().value() : 0;
            return new ElasticPageResult(extractIds(response), totalElement);
        } catch (IOException e) {
            throw new RuntimeException("검색 중 오류가 발생했습니다.", e);
        }
    }

    private List<Long> extractIds(SearchResponse<PlaceDocument> response) {
        return response.hits().hits().stream()
                .map(Hit::id)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();
    }
}
