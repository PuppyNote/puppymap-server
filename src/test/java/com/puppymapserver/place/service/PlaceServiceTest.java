package com.puppymapserver.place.service;

import com.puppymapserver.IntegrationTestSupport;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import com.puppymapserver.place.service.request.PlaceCreateServiceRequest;
import com.puppymapserver.place.service.request.PlaceUpdateServiceRequest;
import com.puppymapserver.place.service.response.PlaceResponse;
import com.puppymapserver.user.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
class PlaceServiceTest extends IntegrationTestSupport {

    @Autowired
    private PlaceService placeService;

    @DisplayName("장소 제보 시 PENDING 상태로 저장된다")
    @Test
    void 장소_제보_요청_시_DB에_PENDING_상태로_저장된다() {
        // given
        User user = createUser("test@example.com", "password");
        PlaceCreateServiceRequest request = PlaceCreateServiceRequest.builder()
                .title("강아지 공원")
                .content("강아지와 함께 산책하기 좋은 공원입니다.")
                .latitude(37.5665)
                .longitude(126.9780)
                .category(PlaceCategory.PARK)
                .largeDogAvailable(true)
                .parkingAvailable(true)
                .offLeashAvailable(false)
                .build();

        // when
        PlaceResponse response = placeService.create(user.getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("강아지 공원");
        assertThat(response.getStatus()).isEqualTo(PlaceStatus.PENDING);
        assertThat(response.getUserId()).isEqualTo(user.getId());
    }

    @DisplayName("장소 제보 시 이미지 URL 목록이 함께 저장된다")
    @Test
    void 장소_제보_요청_시_이미지_URL이_함께_저장된다() {
        // given
        User user = createUser("test2@example.com", "password");
        PlaceCreateServiceRequest request = PlaceCreateServiceRequest.builder()
                .title("카페 공원")
                .content("강아지 동반 카페")
                .latitude(37.5665)
                .longitude(126.9780)
                .category(PlaceCategory.CAFE)
                .imageUrls(java.util.List.of("http://image1.jpg", "http://image2.jpg"))
                .build();

        // when
        PlaceResponse response = placeService.create(user.getId(), request);

        // then
        assertThat(response.getImageUrls()).hasSize(2)
                .containsExactly("http://image1.jpg", "http://image2.jpg");
    }

    @DisplayName("장소 수정 시 제목과 내용이 변경된다")
    @Test
    void 본인_장소_수정_시_제목과_내용이_변경된다() {
        // given
        User user = createUser("test3@example.com", "password");
        Place place = createPlace(user, "원래 제목");

        PlaceUpdateServiceRequest request = PlaceUpdateServiceRequest.builder()
                .placeId(place.getId())
                .userId(user.getId())
                .title("수정된 제목")
                .content("수정된 내용")
                .category(PlaceCategory.TRAIL)
                .largeDogAvailable(false)
                .parkingAvailable(false)
                .offLeashAvailable(true)
                .build();

        // when
        PlaceResponse response = placeService.update(request);

        // then
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.getCategory()).isEqualTo(PlaceCategory.TRAIL);
    }

    @DisplayName("본인이 아닌 장소를 수정하면 예외가 발생한다")
    @Test
    void 타인의_장소_수정_시_예외가_발생한다() {
        // given
        User owner = createUser("owner@example.com", "password");
        User other = createUser("other@example.com", "password");
        Place place = createPlace(owner, "내 장소");

        PlaceUpdateServiceRequest request = PlaceUpdateServiceRequest.builder()
                .placeId(place.getId())
                .userId(other.getId())
                .title("수정 시도")
                .content("수정 내용")
                .category(PlaceCategory.PARK)
                .build();

        // when / then
        assertThatThrownBy(() -> placeService.update(request))
                .isInstanceOf(com.puppymapserver.global.exception.PuppyMapException.class)
                .hasMessageContaining("본인의 제보만 수정할 수 있습니다.");
    }

    @DisplayName("장소 삭제 시 DB에서 제거된다")
    @Test
    void 본인_장소_삭제_시_DB에서_제거된다() {
        // given
        User user = createUser("delete@example.com", "password");
        Place place = createPlace(user, "삭제할 장소");
        Long placeId = place.getId();

        // when
        placeService.delete(placeId, user.getId());

        // then
        assertThat(placeRepository.findById(placeId)).isEmpty();
    }
}
