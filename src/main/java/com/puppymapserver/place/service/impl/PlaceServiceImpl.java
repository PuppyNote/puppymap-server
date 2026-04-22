package com.puppymapserver.place.service.impl;

import com.puppymapserver.place.elasticsearch.PlaceDocument;
import com.puppymapserver.place.elasticsearch.PlaceElasticsearchRepository;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.PlaceTag;
import com.puppymapserver.place.entity.enums.TagType;
import com.puppymapserver.place.repository.PlaceRepository;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.place.service.PlaceService;
import com.puppymapserver.place.service.request.PlaceCreateServiceRequest;
import com.puppymapserver.place.service.request.PlaceUpdateServiceRequest;
import com.puppymapserver.place.service.response.PlaceResponse;
import com.puppymapserver.user.users.entity.User;
import com.puppymapserver.user.users.service.UserReadService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceReadService placeReadService;
    private final UserReadService userReadService;
    private final PlaceElasticsearchRepository placeElasticsearchRepository;

    @Override
    public PlaceResponse create(Long userId, PlaceCreateServiceRequest request) {
        User user = userReadService.findById(userId);

        Place saved = placeRepository.save(request.toEntity(user));
        return PlaceResponse.of(saved);
    }

    @Override
    public PlaceResponse update(PlaceUpdateServiceRequest request) {
        Place place = placeReadService.findByIdOrThrow(request.getPlaceId());
        place.validateOwner(request.getUserId());

        place.update(request.getTitle(), request.getContent(), request.getCategory(),
                request.getLargeDogAvailable(), request.getParkingAvailable(), request.getOffLeashAvailable());

        return PlaceResponse.of(place);
    }

    @Override
    public void delete(Long placeId, Long userId) {
        Place place = placeReadService.findByIdOrThrow(placeId);
        place.validateOwner(userId);
        place.softDelete();
    }

    @Override
    public void addTag(Long placeId, Long userId, TagType tagType) {
        Place place = placeReadService.findByIdOrThrow(placeId);
        User user = userReadService.findById(userId);

        place.getTags().add(PlaceTag.builder()
                .place(place)
                .user(user)
                .tagType(tagType)
                .build());
    }

    @Override
    public void removeTag(Long tagId, Long userId) {
        // PlaceTag 직접 조회는 PlaceTagRepository 필요 - Place 통해 삭제
        // 간단한 구현: place에서 tagId에 해당하는 태그를 찾아 비활성화
        // 실제로는 PlaceTagRepository를 따로 두는 것이 나으나 여기서는 연관관계 통해 처리
        throw new UnsupportedOperationException("태그 삭제는 PlaceTagRepository를 추가하세요.");
    }

    // 관리자 승인 시 ES 인덱싱
    public void indexToElasticsearch(Place place) {
        PlaceDocument doc = PlaceDocument.builder()
                .id(String.valueOf(place.getId()))
                .title(place.getTitle())
                .content(place.getContent())
                .location(new GeoPoint(place.getLatitude(), place.getLongitude()))
                .category(place.getCategory().name())
                .largeDogAvailable(place.getLargeDogAvailable())
                .parkingAvailable(place.getParkingAvailable())
                .offLeashAvailable(place.getOffLeashAvailable())
                .createdDate(place.getCreatedDate())
                .build();
        placeElasticsearchRepository.save(doc);
    }
}
