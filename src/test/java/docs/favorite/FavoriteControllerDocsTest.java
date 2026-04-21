package docs.favorite;

import com.puppymapserver.favorite.controller.FavoriteController;
import com.puppymapserver.favorite.service.FavoriteService;
import com.puppymapserver.favorite.service.response.FavoriteResponse;
import com.puppymapserver.global.security.SecurityService;
import com.puppymapserver.jwt.dto.LoginUserInfo;
import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import com.puppymapserver.place.service.response.PlaceResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FavoriteControllerDocsTest extends RestDocsSupport {

    private final FavoriteService favoriteService = mock(FavoriteService.class);
    private final SecurityService securityService = mock(SecurityService.class);

    @Override
    protected Object initController() {
        return new FavoriteController(favoriteService, securityService);
    }

    @DisplayName("내 즐겨찾기 목록 조회 API")
    @Test
    void 내_즐겨찾기_목록_조회() throws Exception {
        given(securityService.getCurrentLoginUserInfo()).willReturn(LoginUserInfo.of(1L, "USER"));

        PlaceResponse place = PlaceResponse.builder()
                .id(1L)
                .userId(1L)
                .userNickName("제보자")
                .title("강아지 공원")
                .content("강아지와 산책하기 좋은 곳")
                .latitude(37.5665)
                .longitude(126.9780)
                .category(PlaceCategory.PARK)
                .status(PlaceStatus.APPROVED)
                .largeDogAvailable(true)
                .parkingAvailable(true)
                .offLeashAvailable(false)
                .imageUrls(List.of())
                .activeTags(List.of())
                .likeCount(5)
                .createdDate(LocalDateTime.now())
                .build();

        FavoriteResponse favorite = FavoriteResponse.builder()
                .favoriteId(1L)
                .place(place)
                .build();

        given(favoriteService.getMyFavorites(anyLong())).willReturn(List.of(favorite));

        mockMvc.perform(get("/api/v1/users/me/favorites")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("favorite-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data[].favoriteId").type(JsonFieldType.NUMBER).description("즐겨찾기 ID"),
                                fieldWithPath("data[].place.id").type(JsonFieldType.NUMBER).description("장소 ID"),
                                fieldWithPath("data[].place.userId").type(JsonFieldType.NUMBER).description("제보자 ID"),
                                fieldWithPath("data[].place.userNickName").type(JsonFieldType.STRING).description("제보자 닉네임"),
                                fieldWithPath("data[].place.title").type(JsonFieldType.STRING).description("장소 제목"),
                                fieldWithPath("data[].place.content").type(JsonFieldType.STRING).description("장소 설명"),
                                fieldWithPath("data[].place.latitude").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("data[].place.longitude").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("data[].place.category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("data[].place.status").type(JsonFieldType.STRING).description("승인 상태"),
                                fieldWithPath("data[].place.largeDogAvailable").type(JsonFieldType.BOOLEAN).description("대형견 가능 여부"),
                                fieldWithPath("data[].place.parkingAvailable").type(JsonFieldType.BOOLEAN).description("주차 가능 여부"),
                                fieldWithPath("data[].place.offLeashAvailable").type(JsonFieldType.BOOLEAN).description("오프리쉬 가능 여부"),
                                fieldWithPath("data[].place.imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                fieldWithPath("data[].place.activeTags").type(JsonFieldType.ARRAY).description("활성 태그 목록"),
                                fieldWithPath("data[].place.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("data[].place.createdDate").type(JsonFieldType.STRING).description("생성 일시")
                        )
                ));
    }

    @DisplayName("즐겨찾기 추가 API")
    @Test
    void 즐겨찾기_추가() throws Exception {
        given(securityService.getCurrentLoginUserInfo()).willReturn(LoginUserInfo.of(1L, "USER"));
        willDoNothing().given(favoriteService).add(anyLong(), anyLong());

        mockMvc.perform(post("/api/v1/places/{placeId}/favorites", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("favorite-add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("placeId").description("장소 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 없음")
                        )
                ));
    }

    @DisplayName("즐겨찾기 삭제 API")
    @Test
    void 즐겨찾기_삭제() throws Exception {
        given(securityService.getCurrentLoginUserInfo()).willReturn(LoginUserInfo.of(1L, "USER"));
        willDoNothing().given(favoriteService).remove(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/places/{placeId}/favorites", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("favorite-remove",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("placeId").description("장소 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 없음")
                        )
                ));
    }
}
