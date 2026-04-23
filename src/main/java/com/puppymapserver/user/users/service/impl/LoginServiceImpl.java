package com.puppymapserver.user.users.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.puppymapserver.global.email.EmailService;
import com.puppymapserver.global.email.EmailVerification;
import com.puppymapserver.global.email.EmailVerificationRepository;
import com.puppymapserver.global.exception.PuppyMapException;
import com.puppymapserver.jwt.JwtTokenGenerator;
import com.puppymapserver.jwt.dto.JwtToken;
import com.puppymapserver.jwt.dto.LoginUserInfo;
import com.puppymapserver.user.users.entity.User;
import com.puppymapserver.user.users.entity.enums.Role;
import com.puppymapserver.user.users.entity.enums.SnsType;
import com.puppymapserver.user.users.oauth.client.OAuthApiClient;
import com.puppymapserver.user.users.repository.UserRepository;
import com.puppymapserver.user.users.service.LoginService;
import com.puppymapserver.user.users.service.UserReadService;
import com.puppymapserver.user.refreshToken.entity.RefreshToken;
import com.puppymapserver.user.refreshToken.service.RefreshTokenReadService;
import com.puppymapserver.user.users.service.request.EmailSendServiceRequest;
import com.puppymapserver.user.users.service.request.LoginServiceRequest;
import com.puppymapserver.user.users.service.request.OAuthLoginServiceRequest;
import com.puppymapserver.user.users.service.request.PasswordResetServiceRequest;
import com.puppymapserver.user.users.service.request.TokenRefreshServiceRequest;
import com.puppymapserver.user.users.service.response.LoginResponse;
import com.puppymapserver.user.users.service.response.OAuthLoginResponse;
import com.puppymapserver.user.users.service.response.TokenRefreshResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class LoginServiceImpl implements LoginService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final Map<SnsType, OAuthApiClient> clients;
    private final EmailService emailService;
    private final EmailVerificationRepository verificationRepository;

    private final UserRepository userRepository;
    private final UserReadService userReadService;
    private final RefreshTokenReadService refreshTokenReadService;

    public LoginServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenGenerator jwtTokenGenerator,
                            List<OAuthApiClient> clients, UserRepository userRepository, UserReadService userReadService,
                            RefreshTokenReadService refreshTokenReadService, EmailService emailService,
                            EmailVerificationRepository verificationRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthSnsType, Function.identity())
        );
        this.userReadService = userReadService;
        this.refreshTokenReadService = refreshTokenReadService;
        this.emailService = emailService;
        this.verificationRepository = verificationRepository;
    }

    @Override
    public LoginResponse normalLogin(LoginServiceRequest loginServiceRequest) throws JsonProcessingException {
        User user = userReadService.findByEmail(loginServiceRequest.getEmail());     //1. 회원조회
        user.checkSnsType(SnsType.NORMAL);                                     //SNS가입여부확인

        if (!bCryptPasswordEncoder.matches(loginServiceRequest.getPassword(), user.getPassword())) {
            throw new PuppyMapException("아이디 또는 패스워드가 일치하지 않습니다.");
        } //3. 비밀번호 체크

        JwtToken jwtToken = generateToken(user);

        return LoginResponse.of(user, jwtToken);
    }

    @Override
    public OAuthLoginResponse oauthLogin(OAuthLoginServiceRequest oAuthLoginServiceRequest) throws
            JsonProcessingException {
        SnsType snsType = oAuthLoginServiceRequest.getSnsType();

        OAuthApiClient client = clients.get(snsType);
        Optional.ofNullable(client).orElseThrow(() -> new PuppyMapException("존재하지않는 로그인방식입니다."));

        String email = client.getEmail(oAuthLoginServiceRequest.getToken());

        // Optional을 사용하여 트랜잭션 문제 해결
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .snsType(snsType)
                                .role(Role.USER)
                                .useYn("Y")
                                .build()
                ));

        user.checkSnsType(snsType);              //SNS가입여부확인

        JwtToken jwtToken = generateToken(user);

        return OAuthLoginResponse.of(user, jwtToken);
    }

    @Override
    public TokenRefreshResponse refresh(TokenRefreshServiceRequest request) {
        // 1. DB에 저장된 refreshToken인지 검증
        RefreshToken storedToken = refreshTokenReadService.findByRefreshToken(request.getRefreshToken());

        // 2. JWT 서명 및 만료 검증 후 새 토큰 발급
        JwtToken jwtToken = jwtTokenGenerator.generateJwtToken(request.getRefreshToken());

        // 3. DB의 refreshToken을 새 토큰으로 업데이트 (토큰 로테이션)
        storedToken.updateRefreshToken(jwtToken.getRefreshToken());

        return TokenRefreshResponse.from(jwtToken);
    }

    @Override
    public Long sendPasswordResetEmail(EmailSendServiceRequest request) {
        User user = userReadService.findByEmail(request.getEmail());
        checkSnsType(user);
        return emailService.sendVerificationCode(request.getEmail());
    }

    @Override
    public void resetPassword(PasswordResetServiceRequest request) {
        EmailVerification verification = verificationRepository.findById(request.getVerificationId())
                .orElseThrow(() -> new PuppyMapException("유효하지 않은 인증 요청입니다."));
        if (verification.isVerified()) {
            throw new PuppyMapException("이미 사용된 인증번호입니다.");
        }
        if (verification.isExpired()) {
            throw new PuppyMapException("인증번호가 만료되었습니다.");
        }
        if (!verification.getCode().equals(request.getCode())) {
            throw new PuppyMapException("인증번호가 일치하지 않습니다.");
        }
        verification.markVerified();
        User user = userReadService.findByEmail(verification.getEmail());
        checkSnsType(user);
        user.updatePassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
    }

    private JwtToken generateToken(User user) throws JsonProcessingException {
        LoginUserInfo userInfo = LoginUserInfo.of(user.getId(), user.getRole().name());
        JwtToken jwtToken = jwtTokenGenerator.generate(userInfo);
        user.checkRefreshToken(jwtToken, "WEB");
        return jwtToken;
    }

    private void checkSnsType(User user) {
        if (user.getSnsType() != SnsType.NORMAL) {
            throw new PuppyMapException(user.getSnsType().getText() + "로 가입된 계정입니다.");
        }
    }

}
