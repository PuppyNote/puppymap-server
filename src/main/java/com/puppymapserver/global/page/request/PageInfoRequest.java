package com.puppymapserver.global.page.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageInfoRequest {

    private int page = 1;
    private int size = 12;

    public PageInfoServiceRequest toServiceRequest() {
        return PageInfoServiceRequest.builder()
                .page(page)
                .size(size)
                .build();
    }
}
