package docs.place;

import com.puppymapserver.global.security.SecurityService;
import com.puppymapserver.jwt.dto.LoginUserInfo;
import com.puppymapserver.like.service.PlaceLikeService;
import com.puppymapserver.place.controller.PlaceController;
import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import com.puppymapserver.place.service.PlaceReadService;
import com.puppymapserver.place.service.PlaceService;
import com.puppymapserver.place.service.response.PlaceResponse;
import com.puppymapserver.review.service.ReviewService;
import com.puppymapserver.review.service.response.ReviewResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlaceControllerDocsTest extends RestDocsSupport {

    private final PlaceService placeService = mock(PlaceService.class);
    private final PlaceReadService placeReadService = mock(PlaceReadService.class);
    private final ReviewService reviewService = mock(ReviewService.class);
    private final PlaceLikeService placeLikeService = mock(PlaceLikeService.class);
    private final SecurityService securityService = mock(SecurityService.class);

    @Override
    protected Object initController() {
        return new PlaceController(placeService, placeReadService, reviewService, placeLikeService, securityService);
    }

    @DisplayName("승인된 장소 목록 조회 API")
    @Test
    void 장소_목록_조회() throws Exception {
        PlaceResponse place = PlaceResponse.builder()
                .id(1L)
                .userId(1L)
                .userNickName("테스터")
                .title("강아지 공원")
                .content("강아지와 함께 산책하기 좋은 곳")
                .latitude(37.5665)
                .longitude(126.9780)
                .category(PlaceCategory.PARK)
                .status(PlaceStatus.APPROVED)
                .largeDogAvailable(true)
                .parkingAvailable(true)
                .offLeashAvailable(false)
                .imageUrls(List.of("http://image1.jpg"))
                .activeTags(List.of())
                .likeCount(10)
                .createdDate(LocalDateTime.now())
                .build();

        given(placeReadService.getApprovedPlaces(any(), any(), any(), any()))
                .willReturn(List.of(place));

        mockMvc.perform(get("/api/v1/places")
                        .param("category", "PARK")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("place-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("category").description("장소 카테고리 (PARK, TRAIL, CAFE, ETC)").optional(),
                                parameterWithName("largeDog").description("대형견 가능 여부").optional(),
                                parameterWithName("parking").description("주차 가능 여부").optional(),
                                parameterWithName("offLeash").description("오프리쉬 구역 여부").optional()
                        ),
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
                                fieldWithPath("data[].activeTags").type(JsonFieldType.ARRAY).description("활성 태그 목록"),
                                fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("data[].createdDate").type(JsonFieldType.STRING).description("생성 일시")
                        )
                ));
    }

    @DisplayName("장소 상세 조회 API")
    @Test
    void 장소_상세_조회() throws Exception {
        PlaceResponse place = PlaceResponse.builder()
                .id(1L)
                .userId(1L)
                .userNickName("테스터")
                .title("강아지 공원")
                .content("강아지와 함께 산책하기 좋은 곳")
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

        given(placeReadService.getApprovedPlace(anyLong())).willReturn(place);

        mockMvc.perform(get("/api/v1/places/{placeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("place-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("placeId").description("장소 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("장소 ID"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("제보자 ID"),
                                fieldWithPath("data.userNickName").type(JsonFieldType.STRING).description("제보자 닉네임"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("장소 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("장소 설명"),
                                fieldWithPath("data.latitude").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("data.longitude").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("승인 상태"),
                                fieldWithPath("data.largeDogAvailable").type(JsonFieldType.BOOLEAN).description("대형견 가능 여부"),
                                fieldWithPath("data.parkingAvailable").type(JsonFieldType.BOOLEAN).description("주차 가능 여부"),
                                fieldWithPath("data.offLeashAvailable").type(JsonFieldType.BOOLEAN).description("오프리쉬 가능 여부"),
                                fieldWithPath("data.imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                fieldWithPath("data.activeTags").type(JsonFieldType.ARRAY).description("활성 태그 목록"),
                                fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("data.createdDate").type(JsonFieldType.STRING).description("생성 일시")
                        )
                ));
    }

    @DisplayName("장소 제보 API")
    @Test
    void 장소_제보() throws Exception {
        given(securityService.getCurrentLoginUserInfo()).willReturn(LoginUserInfo.of(1L, "USER"));

        PlaceResponse response = PlaceResponse.builder()
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
                .activeTags(List.of())
                .likeCount(0)
                .createdDate(LocalDateTime.now())
                .build();

        given(placeService.create(anyLong(), any())).willReturn(response);

        String requestBody = objectMapper.writeValueAsString(new java.util.HashMap<>() {{
            put("title", "강아지 공원");
            put("content", "강아지와 함께 산책하기 좋은 곳");
            put("latitude", 37.5665);
            put("longitude", 126.9780);
            put("category", "PARK");
            put("largeDogAvailable", true);
            put("parkingAvailable", true);
            put("offLeashAvailable", false);
        }});

        mockMvc.perform(post("/api/v1/places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("place-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("장소 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("장소 설명"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리 (PARK, TRAIL, CAFE, ETC)"),
                                fieldWithPath("largeDogAvailable").type(JsonFieldType.BOOLEAN).description("대형견 가능 여부").optional(),
                                fieldWithPath("parkingAvailable").type(JsonFieldType.BOOLEAN).description("주차 가능 여부").optional(),
                                fieldWithPath("offLeashAvailable").type(JsonFieldType.BOOLEAN).description("오프리쉬 가능 여부").optional()
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("장소 ID"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("제보자 ID"),
                                fieldWithPath("data.userNickName").type(JsonFieldType.STRING).description("제보자 닉네임"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("장소 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("장소 설명"),
                                fieldWithPath("data.latitude").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("data.longitude").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("승인 상태 (PENDING)"),
                                fieldWithPath("data.largeDogAvailable").type(JsonFieldType.BOOLEAN).description("대형견 가능 여부"),
                                fieldWithPath("data.parkingAvailable").type(JsonFieldType.BOOLEAN).description("주차 가능 여부"),
                                fieldWithPath("data.offLeashAvailable").type(JsonFieldType.BOOLEAN).description("오프리쉬 가능 여부"),
                                fieldWithPath("data.imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                fieldWithPath("data.activeTags").type(JsonFieldType.ARRAY).description("활성 태그 목록"),
                                fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("data.createdDate").type(JsonFieldType.STRING).description("생성 일시")
                        )
                ));
    }

    @DisplayName("장소 리뷰 목록 조회 API")
    @Test
    void 장소_리뷰_목록_조회() throws Exception {
        ReviewResponse review = ReviewResponse.builder()
                .id(1L)
                .placeId(1L)
                .userId(1L)
                .userNickName("리뷰어")
                .rating(5)
                .comment("너무 좋아요!")
                .createdDate(LocalDateTime.now())
                .build();

        given(reviewService.getByPlace(anyLong())).willReturn(List.of(review));

        mockMvc.perform(get("/api/v1/places/{placeId}/reviews", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("place-reviews",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("placeId").description("장소 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                fieldWithPath("data[].placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("작성자 ID"),
                                fieldWithPath("data[].userNickName").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("data[].rating").type(JsonFieldType.NUMBER).description("평점 (1~5)"),
                                fieldWithPath("data[].comment").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("data[].createdDate").type(JsonFieldType.STRING).description("작성 일시")
                        )
                ));
    }
}
