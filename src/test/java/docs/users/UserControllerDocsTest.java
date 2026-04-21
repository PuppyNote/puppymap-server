package docs.users;

import com.puppymapserver.user.users.controller.UserController;
import com.puppymapserver.user.users.controller.request.EmailSendRequest;
import com.puppymapserver.user.users.controller.request.SignUpRequest;
import com.puppymapserver.user.users.controller.request.UserProfileUpdateRequest;
import com.puppymapserver.user.users.service.UserReadService;
import com.puppymapserver.user.users.service.UserService;
import com.puppymapserver.user.users.service.response.SignUpResponse;
import com.puppymapserver.user.users.service.response.UserProfileResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerDocsTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);
    private final UserReadService userReadService = mock(UserReadService.class);

    @Override
    protected Object initController() {
        return new UserController(userService, userReadService);
    }

    @DisplayName("회원가입 API")
    @Test
    void 회원가입() throws Exception {
        given(userService.signUp(any())).willReturn(
                SignUpResponse.builder()
                        .email("test@example.com")
                        .nickName("테스터")
                        .build()
        );

        String requestBody = objectMapper.writeValueAsString(
                SignUpRequest.builder()
                        .email("test@example.com")
                        .password("password123")
                        .nickName("테스터")
                        .build()
        );

        mockMvc.perform(post("/api/v1/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("user-signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호 (최소 8자)"),
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("닉네임 (최대 20자)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.nickName").type(JsonFieldType.STRING).description("닉네임")
                        )
                ));
    }

    @DisplayName("이메일 인증 전송 API")
    @Test
    void 이메일_인증_전송() throws Exception {
        given(userService.sendVerificationEmail(any())).willReturn("인증 이메일이 전송되었습니다.");

        String requestBody = objectMapper.writeValueAsString(
                EmailSendRequest.builder()
                        .email("test@example.com")
                        .build()
        );

        mockMvc.perform(post("/api/v1/user/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-email-send",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("인증할 이메일")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("전송 결과 메시지")
                        )
                ));
    }

    @DisplayName("내 프로필 조회 API")
    @Test
    void 내_프로필_조회() throws Exception {
        given(userReadService.getMyProfile()).willReturn(
                UserProfileResponse.of(
                        com.puppymapserver.user.users.entity.User.builder()
                                .email("test@example.com")
                                .nickName("테스터")
                                .build(),
                        "https://s3.example.com/profile.jpg"
                )
        );

        mockMvc.perform(get("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-profile-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data.userId").type(JsonFieldType.NULL).description("사용자 ID"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.nickName").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.profileUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL")
                        )
                ));
    }

    @DisplayName("프로필 수정 API")
    @Test
    void 프로필_수정() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                UserProfileUpdateRequest.builder()
                        .nickName("새닉네임")
                        .profileUrl("https://s3.example.com/new-profile.jpg")
                        .build()
        );

        mockMvc.perform(patch("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-profile-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickName").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("profileUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL").optional()
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터").optional()
                        )
                ));
    }

    @DisplayName("회원 탈퇴 API")
    @Test
    void 회원_탈퇴() throws Exception {
        mockMvc.perform(delete("/api/v1/user/withdraw")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-withdraw",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터").optional()
                        )
                ));
    }
}
