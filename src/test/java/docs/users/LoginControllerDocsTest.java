package docs.users;

import com.puppymapserver.jwt.dto.JwtToken;
import com.puppymapserver.user.users.controller.LoginController;
import com.puppymapserver.user.users.controller.request.EmailSendRequest;
import com.puppymapserver.user.users.controller.request.LoginOauthRequest;
import com.puppymapserver.user.users.controller.request.LoginRequest;
import com.puppymapserver.user.users.controller.request.PasswordResetRequest;
import com.puppymapserver.user.users.entity.enums.SnsType;
import com.puppymapserver.user.users.service.LoginService;
import com.puppymapserver.user.users.service.response.LoginResponse;
import com.puppymapserver.user.users.service.response.OAuthLoginResponse;
import com.puppymapserver.user.users.service.response.TokenRefreshResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerDocsTest extends RestDocsSupport {

    private final LoginService loginService = mock(LoginService.class);

    @Override
    protected Object initController() {
        return new LoginController(loginService);
    }

    @DisplayName("일반 로그인 API")
    @Test
    void 일반_로그인() throws Exception {
        given(loginService.normalLogin(any())).willReturn(
                LoginResponse.builder()
                        .email("test@example.com")
                        .accessToken("eyJhbGciOiJIUzI1NiJ9.access")
                        .refreshToken("eyJhbGciOiJIUzI1NiJ9.refresh")
                        .role(com.puppymapserver.user.users.entity.enums.Role.USER)
                        .build()
        );

        String requestBody = objectMapper.writeValueAsString(
                LoginRequest.builder()
                        .email("test@example.com")
                        .password("password123")
                        .build()
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("auth-login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("사용자 권한 (USER, ADMIN)")
                        )
                ));
    }

    @DisplayName("OAuth 로그인 API")
    @Test
    void OAuth_로그인() throws Exception {
        given(loginService.oauthLogin(any())).willReturn(
                OAuthLoginResponse.builder()
                        .email("test@example.com")
                        .accessToken("eyJhbGciOiJIUzI1NiJ9.access")
                        .refreshToken("eyJhbGciOiJIUzI1NiJ9.refresh")
                        .role(com.puppymapserver.user.users.entity.enums.Role.USER)
                        .build()
        );

        String requestBody = objectMapper.writeValueAsString(
                LoginOauthRequest.builder()
                        .token("kakao-access-token-xxx")
                        .snsType(SnsType.KAKAO)
                        .build()
        );

        mockMvc.perform(post("/api/v1/auth/oauth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("auth-oauth-login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("token").type(JsonFieldType.STRING).description("SNS 액세스 토큰"),
                                fieldWithPath("snsType").type(JsonFieldType.STRING).description("SNS 유형 (KAKAO, GOOGLE, APPLE)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("사용자 권한 (USER, ADMIN)")
                        )
                ));
    }

    @DisplayName("토큰 갱신 API")
    @Test
    void 토큰_갱신() throws Exception {
        given(loginService.refresh(any())).willReturn(
                TokenRefreshResponse.from(
                        JwtToken.of("eyJhbGciOiJIUzI1NiJ9.new-access",
                                "eyJhbGciOiJIUzI1NiJ9.new-refresh", "Bearer", 3600L)
                )
        );

        String requestBody = objectMapper.writeValueAsString(
                Map.of("refreshToken", "eyJhbGciOiJIUzI1NiJ9.refresh")
        );

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("auth-refresh",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("새 액세스 토큰"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("새 리프레시 토큰")
                        )
                ));
    }

    @DisplayName("비밀번호 재설정 이메일 전송 API")
    @Test
    void 비밀번호_재설정_이메일_전송() throws Exception {
        given(loginService.sendPasswordResetEmail(any())).willReturn(1L);

        String requestBody = objectMapper.writeValueAsString(
                EmailSendRequest.builder()
                        .email("test@example.com")
                        .build()
        );

        mockMvc.perform(post("/api/v1/auth/password/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("auth-password-email-send",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("인증 ID (비밀번호 재설정 시 사용)")
                        )
                ));
    }

    @DisplayName("비밀번호 재설정 API")
    @Test
    void 비밀번호_재설정() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                PasswordResetRequest.builder()
                        .verificationId(1L)
                        .code("123456")
                        .newPassword("newPassword123")
                        .build()
        );

        mockMvc.perform(post("/api/v1/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("auth-password-reset",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("verificationId").type(JsonFieldType.NUMBER).description("인증 ID"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("인증번호"),
                                fieldWithPath("newPassword").type(JsonFieldType.STRING).description("새 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("HTTP 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터").optional()
                        )
                ));
    }
}
