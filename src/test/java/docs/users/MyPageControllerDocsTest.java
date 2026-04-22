package docs.users;

import com.puppymapserver.global.security.SecurityService;
import com.puppymapserver.jwt.dto.LoginUserInfo;
import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.place.service.response.PlaceResponse;
import com.puppymapserver.user.users.controller.MyPageController;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MyPageControllerDocsTest extends RestDocsSupport {

    private final PlaceReadService placeReadService = mock(PlaceReadService.class);
    private final SecurityService securityService = mock(SecurityService.class);

    @Override
    protected Object initController() {
        return new MyPageController(placeReadService, securityService);
    }

    @DisplayName("내 제보 목록 조회 API")
    @Test
    void 내_제보_목록_조회() throws Exception {
        given(securityService.getCurrentLoginUserInfo()).willReturn(LoginUserInfo.of(1L, "USER"));

        PlaceResponse place = PlaceResponse.builder()
                .id(1L)
                .userId(1L)
                .userNickName("테스터")
                .title("강아지 공원")
                .content("강아지와 함께 산책하기 좋은 곳")
                .latitude(37.5665)
                .longitude(126.9780)
                .category(PlaceCategory.PARK)
                .status(PlaceStatus.PENDING)
                .largeDogAvailable(true)
                .parkingAvailable(true)
                .offLeashAvailable(false)
                .imageUrls(List.of())
                .likeCount(0)
                .createdDate(LocalDateTime.now())
                .build();

        given(placeReadService.getMyPlaces(anyLong()))
                .willReturn(List.of(place));

        mockMvc.perform(get("/api/v1/users/me/places")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("mypage-places",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("장소 ID"),
                                fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("제보자 ID"),
                                fieldWithPath("data[].userNickName").type(JsonFieldType.STRING).description("제보자 닉네임"),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING).description("장소 제목"),
                                fieldWithPath("data[].content").type(JsonFieldType.STRING).description("장소 설명"),
                                fieldWithPath("data[].latitude").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("data[].longitude").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("data[].category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description("승인 상태"),
                                fieldWithPath("data[].largeDogAvailable").type(JsonFieldType.BOOLEAN).description("대형견 가능 여부"),
                                fieldWithPath("data[].parkingAvailable").type(JsonFieldType.BOOLEAN).description("주차 가능 여부"),
                                fieldWithPath("data[].offLeashAvailable").type(JsonFieldType.BOOLEAN).description("오프리쉬 가능 여부"),
                                fieldWithPath("data[].imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("data[].createdDate").type(JsonFieldType.STRING).description("생성 일시")
                        )
                ));
    }
}
