package com.puppymapserver;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.puppymapserver.jwt.dto.LoginUserInfo;
import com.puppymapserver.place.elasticsearch.PlaceElasticsearchRepository;
import com.puppymapserver.place.entity.Place;
import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.repository.PlaceRepository;
import com.puppymapserver.user.users.entity.User;
import com.puppymapserver.user.users.entity.enums.Role;
import com.puppymapserver.user.users.entity.enums.SnsType;
import com.puppymapserver.user.users.oauth.feign.google.GoogleApiFeignCall;
import com.puppymapserver.user.users.oauth.feign.kakao.KakaoApiFeignCall;
import com.puppymapserver.user.users.repository.UserRepository;
import com.puppymapserver.storage.service.S3StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    @MockitoBean
    protected KakaoApiFeignCall kakaoApiFeignCall;

    @BeforeEach
    void stubS3StorageService() {
        Mockito.when(s3StorageService.getPlaceCloudFrontUrl(Mockito.anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @MockitoBean
    protected GoogleApiFeignCall googleApiFeignCall;

    @MockitoBean
    protected S3StorageService s3StorageService;

    @MockitoBean
    protected ElasticsearchClient elasticsearchClient;

    @MockitoBean
    protected PlaceElasticsearchRepository placeElasticsearchRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PlaceRepository placeRepository;

    protected User createUser(String email, String password) {
        return userRepository.save(User.builder()
                .email(email)
                .password(password)
                .nickName("테스터")
                .snsType(SnsType.NORMAL)
                .role(Role.USER)
                .useYn("Y")
                .build());
    }

    protected User createAdminUser(String email, String password) {
        return userRepository.save(User.builder()
                .email(email)
                .password(password)
                .nickName("관리자")
                .snsType(SnsType.NORMAL)
                .role(Role.ADMIN)
                .useYn("Y")
                .build());
    }

    protected LoginUserInfo createLoginUserInfo(Long userId, String role) {
        return LoginUserInfo.of(userId, role);
    }

    protected Place createPlace(User user, String title) {
        return placeRepository.save(Place.builder()
                .user(user)
                .title(title)
                .content("테스트 내용")
                .latitude(37.5665)
                .longitude(126.9780)
                .category(PlaceCategory.PARK)
                .largeDogAvailable(true)
                .parkingAvailable(true)
                .offLeashAvailable(false)
                .build());
    }
}
