package com.puppymapserver.user.users.service.impl;

import com.puppymapserver.global.email.EmailService;
import com.puppymapserver.global.exception.PuppyMapException;
import com.puppymapserver.global.security.SecurityService;
import com.puppymapserver.user.users.entity.User;
import com.puppymapserver.user.users.repository.UserRepository;
import com.puppymapserver.user.users.service.UserReadService;
import com.puppymapserver.user.users.service.UserService;
import com.puppymapserver.user.users.service.request.EmailSendServiceRequest;
import com.puppymapserver.user.users.service.request.SignUpServiceRequest;
import com.puppymapserver.user.users.service.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserReadService userReadService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    @Override
    public SignUpResponse signUp(SignUpServiceRequest request) {
        checkExistEmail(request.getEmail());
        return SignUpResponse.of(userRepository.save(request.toEntity(passwordEncoder.encode(request.getPassword()))));
    }

    @Override
    public String sendVerificationEmail(EmailSendServiceRequest request) {
        checkExistEmail(request.getEmail());
        return emailService.sendVerificationCode(request.getEmail());
    }

    @Override
    public void withdraw() {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        User user = userReadService.findById(userId);
        user.withdraw();
    }

    private void checkExistEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new PuppyMapException("이미 사용 중인 이메일입니다.");
        }
    }
}
