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

    @Override
    protected Object initController() {
        return new UserController(userService);
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
