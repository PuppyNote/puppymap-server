package com.puppymapserver.review.service.impl;

import com.puppymapserver.global.exception.NotFoundException;
import com.puppymapserver.review.entity.Review;
import com.puppymapserver.review.repository.ReviewJpaRepository;
import com.puppymapserver.review.service.ReviewReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewReadServiceImpl implements ReviewReadService {

    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Review findByIdOrThrow(Long reviewId) {
        return reviewJpaRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다."));
    }
}
