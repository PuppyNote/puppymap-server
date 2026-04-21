package com.puppymapserver.place.service;

import com.puppymapserver.place.service.request.PlaceCreateServiceRequest;
import com.puppymapserver.place.service.request.PlaceUpdateServiceRequest;
import com.puppymapserver.place.service.response.PlaceResponse;
import com.puppymapserver.place.entity.enums.TagType;

public interface PlaceService {
    PlaceResponse create(Long userId, PlaceCreateServiceRequest request);
    PlaceResponse update(PlaceUpdateServiceRequest request);
    void delete(Long placeId, Long userId);
    void addTag(Long placeId, Long userId, TagType tagType);
    void removeTag(Long tagId, Long userId);
}
