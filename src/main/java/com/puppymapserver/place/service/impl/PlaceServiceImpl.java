package com.puppymapserver.place.service.impl;

import com.puppymapserver.global.exception.NotFoundException;
import com.puppymapserver.global.exception.PuppyMapException;
import com.puppymapserver.place.elasticsearch.PlaceDocument;
import com.puppymapserver.place.elasticsearch.PlaceElasticsearchRepository;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.PlaceImage;
import com.puppymapserver.place.entity.PlaceTag;
import com.puppymapserver.place.entity.enums.TagType;
import com.puppymapserver.place.repository.PlaceRepository;
import com.puppymapserver.place.service.PlaceService;
import com.puppymapserver.place.service.request.PlaceCreateServiceRequest;
import com.puppymapserver.place.service.request.PlaceUpdateServiceRequest;
import com.puppymapserver.place.service.response.PlaceResponse;
import com.puppymapserver.user.users.repository.UserRepository;
import com.puppymapserver.user.users.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final PlaceElasticsearchRepository placeElasticsearchRepository;

    @Override
    public PlaceResponse create(Long userId, PlaceCreateServiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        Place place = Place.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .category(request.getCategory())
                .largeDogAvailable(request.getLargeDogAvailable())
                .parkingAvailable(request.getParkingAvailable())
                .offLeashAvailable(request.getOffLeashAvailable())
                .build();

        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                place.getImages().add(PlaceImage.builder()
                        .place(place)
                        .imageUrl(request.getImageUrls().get(i))
                        .sortOrder(i)
                        .build());
            }
        }

        Place saved = placeRepository.save(place);
        return PlaceResponse.of(saved);
    }

    @Override
    public PlaceResponse update(PlaceUpdateServiceRequest request) {
        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));

        if (!place.getUser().getId().equals(request.getUserId())) {
            throw new PuppyMapException("본인의 제보만 수정할 수 있습니다.");
        }

        place.update(request.getTitle(), request.getContent(), request.getCategory(),
                request.getLargeDogAvailable(), request.getParkingAvailable(), request.getOffLeashAvailable());

        return PlaceResponse.of(place);
    }

    @Override
    public void delete(Long placeId, Long userId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));

        if (!place.getUser().getId().equals(userId)) {
            throw new PuppyMapException("본인의 제보만 삭제할 수 있습니다.");
        }

        placeElasticsearchRepository.deleteById(String.valueOf(placeId));
        placeRepository.delete(place);
    }

    @Override
    public void addTag(Long placeId, Long userId, TagType tagType) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

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
