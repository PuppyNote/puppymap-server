package docs.review;

import com.puppymapserver.global.security.SecurityService;
import com.puppymapserver.jwt.dto.LoginUserInfo;
import com.puppymapserver.review.controller.ReviewController;
import com.puppymapserver.review.service.ReviewService;
import com.puppymapserver.review.service.response.ReviewResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
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

class ReviewControllerDocsTest extends RestDocsSupport {

    private final ReviewService reviewService = mock(ReviewService.class);
    private final SecurityService securityService = mock(SecurityService.class);

    @Override
    protected Object initController() {
        return new ReviewController(reviewService, securityService);
    }

    @DisplayName("리뷰 수정 API")
    @Test
    void 리뷰_수정() throws Exception {
        given(securityService.getCurrentLoginUserInfo()).willReturn(LoginUserInfo.of(1L, "USER"));

        ReviewResponse response = ReviewResponse.builder()
                .id(1L)
                .placeId(1L)
                .userId(1L)
                .userNickName("리뷰어")
                .rating(5)
                .comment("수정된 리뷰입니다.")
                .createdDate(LocalDateTime.now())
                .build();

        given(reviewService.update(any())).willReturn(response);

        String requestBody = objectMapper.writeValueAsString(new HashMap<>() {{
            put("rating", 5);
            put("comment", "수정된 리뷰입니다.");
        }});

        mockMvc.perform(patch("/api/v1/reviews/{reviewId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("review-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 ID")
                        ),
                        requestFields(
                                fieldWithPath("rating").type(JsonFieldType.NUMBER).description("평점 (1~5)"),
                                fieldWithPath("comment").type(JsonFieldType.STRING).description("리뷰 내용")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                fieldWithPath("data.placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("작성자 ID"),
                                fieldWithPath("data.userNickName").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("data.rating").type(JsonFieldType.NUMBER).description("평점"),
                                fieldWithPath("data.comment").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("data.createdDate").type(JsonFieldType.STRING).description("작성 일시")
                        )
                ));
    }

    @DisplayName("리뷰 삭제 API")
    @Test
    void 리뷰_삭제() throws Exception {
        given(securityService.getCurrentLoginUserInfo()).willReturn(LoginUserInfo.of(1L, "USER"));
        willDoNothing().given(reviewService).delete(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/reviews/{reviewId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("review-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("reviewId").description("삭제할 리뷰 ID")
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
