package com.puppymapserver.review.service;

import com.puppymapserver.IntegrationTestSupport;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import com.puppymapserver.review.service.request.ReviewCreateServiceRequest;
import com.puppymapserver.review.service.request.ReviewUpdateServiceRequest;
import com.puppymapserver.review.service.response.ReviewResponse;
import com.puppymapserver.user.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class ReviewServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReviewService reviewService;

    @DisplayName("승인된 장소에 리뷰를 작성할 수 있다")
    @Test
    void 승인된_장소에_리뷰_작성_시_DB에_저장된다() {
        // given
        User user = createUser("reviewer@example.com", "password");
        Place place = createApprovedPlace(user, "공원");

        ReviewCreateServiceRequest request = ReviewCreateServiceRequest.builder()
                .placeId(place.getId())
                .userId(user.getId())
                .rating(5)
                .comment("정말 좋은 공원이에요!")
                .build();

        // when
        ReviewResponse response = reviewService.create(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getComment()).isEqualTo("정말 좋은 공원이에요!");
        assertThat(response.getUserId()).isEqualTo(user.getId());
    }

    @DisplayName("리뷰 수정 시 평점과 내용이 변경된다")
    @Test
    void 본인_리뷰_수정_시_평점과_내용이_변경된다() {
        // given
        User user = createUser("reviewer2@example.com", "password");
        Place place = createApprovedPlace(user, "산책로");

        ReviewCreateServiceRequest createRequest = ReviewCreateServiceRequest.builder()
                .placeId(place.getId())
                .userId(user.getId())
                .rating(3)
                .comment("보통이에요")
                .build();
        ReviewResponse created = reviewService.create(createRequest);

        ReviewUpdateServiceRequest updateRequest = ReviewUpdateServiceRequest.builder()
                .reviewId(created.getId())
                .userId(user.getId())
                .rating(5)
                .comment("다시 가보니 너무 좋아요!")
                .build();

        // when
        ReviewResponse updated = reviewService.update(updateRequest);

        // then
        assertThat(updated.getRating()).isEqualTo(5);
        assertThat(updated.getComment()).isEqualTo("다시 가보니 너무 좋아요!");
    }

    @DisplayName("본인이 아닌 리뷰를 수정하면 예외가 발생한다")
    @Test
    void 타인의_리뷰_수정_시_예외가_발생한다() {
        // given
        User owner = createUser("owner@example.com", "password");
        User other = createUser("other@example.com", "password");
        Place place = createApprovedPlace(owner, "공원");

        ReviewCreateServiceRequest createRequest = ReviewCreateServiceRequest.builder()
                .placeId(place.getId())
                .userId(owner.getId())
                .rating(4)
                .comment("좋아요")
                .build();
        ReviewResponse created = reviewService.create(createRequest);

        ReviewUpdateServiceRequest updateRequest = ReviewUpdateServiceRequest.builder()
                .reviewId(created.getId())
                .userId(other.getId())
                .rating(1)
                .comment("나쁜 곳")
                .build();

        // when / then
        assertThatThrownBy(() -> reviewService.update(updateRequest))
                .isInstanceOf(com.puppymapserver.global.exception.PuppyMapException.class)
                .hasMessageContaining("본인의 리뷰만 수정할 수 있습니다.");
    }

    @DisplayName("장소에 달린 리뷰 목록을 조회할 수 있다")
    @Test
    void 장소의_리뷰_목록_조회_시_해당_장소_리뷰만_반환된다() {
        // given
        User user = createUser("list@example.com", "password");
        Place place = createApprovedPlace(user, "공원");

        reviewService.create(ReviewCreateServiceRequest.builder()
                .placeId(place.getId()).userId(user.getId()).rating(5).comment("최고").build());
        reviewService.create(ReviewCreateServiceRequest.builder()
                .placeId(place.getId()).userId(user.getId()).rating(4).comment("좋아요").build());

        // when
        List<ReviewResponse> reviews = reviewService.getByPlace(place.getId());

        // then
        assertThat(reviews).hasSize(2)
                .extracting(ReviewResponse::getPlaceId)
                .containsOnly(place.getId());
    }

    private Place createApprovedPlace(User user, String title) {
        Place place = createPlace(user, title);
        place.approve();
        return placeRepository.save(place);
    }
}
