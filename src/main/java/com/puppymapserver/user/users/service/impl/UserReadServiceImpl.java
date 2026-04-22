package com.puppymapserver.user.users.service.impl;

import com.puppymapserver.global.exception.PuppyMapException;
import com.puppymapserver.user.users.entity.User;
import com.puppymapserver.user.users.repository.UserRepository;
import com.puppymapserver.user.users.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReadServiceImpl implements UserReadService {

    private static final String UNKNOWN_USER = "해당 회원은 존재하지 않습니다.";

    private final UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new PuppyMapException(UNKNOWN_USER));
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PuppyMapException(UNKNOWN_USER));
    }

    @Override
    public List<User> findAllByEmailLike(String email) {
        return userRepository.findAllByEmailLike(email);
    }
}
