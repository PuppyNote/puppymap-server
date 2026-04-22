package docs.place;

import com.puppymapserver.place.controller.AdminPlaceController;
import com.puppymapserver.place.entity.enums.PlaceCategory;
import com.puppymapserver.place.entity.enums.PlaceStatus;
import com.puppymapserver.place.service.AdminPlaceService;
import com.puppymapserver.place.service.response.PlaceResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

class AdminPlaceControllerDocsTest extends RestDocsSupport {

    private final AdminPlaceService adminPlaceService = mock(AdminPlaceService.class);

    @Override
    protected Object initController() {
        return new AdminPlaceController(adminPlaceService);
    }

    @DisplayName("관리자 제보 목록 조회 API")
    @Test
    void 관리자_제보_목록_조회() throws Exception {
        PlaceResponse place = PlaceResponse.builder()
                .id(1L)
                .userId(1L)
                .userNickName("제보자")
                .title("강아지 공원")
                .content("강아지와 산책하기 좋은 곳")
                .latitude(37.5665)
                .longitude(126.9780)
                .category(PlaceCategory.PARK)
                .status(PlaceStatus.PENDING)
                .largeDogAvailable(true)
                .parkingAvailable(false)
                .offLeashAvailable(false)
                .imageUrls(List.of())

                .likeCount(0)
                .createdDate(LocalDateTime.now())
                .build();

        given(adminPlaceService.getAllPlaces(any())).willReturn(List.of(place));

        mockMvc.perform(get("/api/v1/admin/places")
                        .param("status", "PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-place-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("status").description("장소 상태 필터 (PENDING, APPROVED, REJECTED)").optional()
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
                                fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                fieldWithPath("data[].createdDate").type(JsonFieldType.STRING).description("생성 일시")
                        )
                ));
    }

    @DisplayName("장소 승인 API")
    @Test
    void 장소_승인() throws Exception {
        willDoNothing().given(adminPlaceService).approve(anyLong());

        mockMvc.perform(patch("/api/v1/admin/places/{placeId}/approve", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-place-approve",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("placeId").description("승인할 장소 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 없음")
                        )
                ));
    }

    @DisplayName("장소 거절 API")
    @Test
    void 장소_거절() throws Exception {
        willDoNothing().given(adminPlaceService).reject(anyLong(), anyString());

        mockMvc.perform(patch("/api/v1/admin/places/{placeId}/reject", 1L)
                        .queryParam("reason", "이미 폐업한 장소입니다.")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-place-reject",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("placeId").description("거절할 장소 ID")
                        ),
                        queryParameters(
                                parameterWithName("reason").description("거절 사유")
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
