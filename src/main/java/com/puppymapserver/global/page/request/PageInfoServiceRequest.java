package com.puppymapserver.global.page.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
public class PageInfoServiceRequest {

    private final int page;
    private final int size;

    @Builder
    private PageInfoServiceRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public Pageable toPageable() {
        return PageRequest.of(page - 1, size);
    }
}
