package com.puppymapserver.favorite.service;

import com.puppymapserver.IntegrationTestSupport;
import com.puppymapserver.favorite.service.response.FavoriteResponse;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.user.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class FavoriteServiceTest extends IntegrationTestSupport {

    @Autowired
    private FavoriteService favoriteService;

    @DisplayName("즐겨찾기 추가 시 목록에서 조회된다")
    @Test
    void 즐겨찾기_추가_시_내_즐겨찾기_목록에서_조회된다() {
        // given
        User user = createUser("fav@example.com", "password");
        Place place = createApprovedPlace(user, "즐겨찾기 공원");

        // when
        favoriteService.add(place.getId(), user.getId());
        List<FavoriteResponse> favorites = favoriteService.getMyFavorites(user.getId());

        // then
        assertThat(favorites).hasSize(1);
        assertThat(favorites.get(0).getPlace().getId()).isEqualTo(place.getId());
    }

    @DisplayName("이미 즐겨찾기된 장소를 다시 추가하면 예외가 발생한다")
    @Test
    void 이미_즐겨찾기된_장소_재추가_시_예외가_발생한다() {
        // given
        User user = createUser("fav2@example.com", "password");
        Place place = createApprovedPlace(user, "즐겨찾기 공원2");
        favoriteService.add(place.getId(), user.getId());

        // when / then
        assertThatThrownBy(() -> favoriteService.add(place.getId(), user.getId()))
                .isInstanceOf(com.puppymapserver.global.exception.PuppyMapException.class)
                .hasMessageContaining("이미 즐겨찾기에 추가된 장소입니다.");
    }

    @DisplayName("즐겨찾기 삭제 시 목록에서 제거된다")
    @Test
    void 즐겨찾기_삭제_시_내_즐겨찾기_목록에서_제거된다() {
        // given
        User user = createUser("fav3@example.com", "password");
        Place place = createApprovedPlace(user, "즐겨찾기 공원3");
        favoriteService.add(place.getId(), user.getId());

        // when
        favoriteService.remove(place.getId(), user.getId());
        List<FavoriteResponse> favorites = favoriteService.getMyFavorites(user.getId());

        // then
        assertThat(favorites).isEmpty();
    }

    private Place createApprovedPlace(User user, String title) {
        Place place = createPlace(user, title);
        place.approve();
        return placeRepository.save(place);
    }
}
