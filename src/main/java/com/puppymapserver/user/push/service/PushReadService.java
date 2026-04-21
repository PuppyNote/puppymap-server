package com.puppymapserver.user.push.service;

import com.puppymapserver.user.push.entity.Push;

import java.util.List;
import java.util.Optional;

public interface PushReadService {

    Optional<Push> findByUserId(Long userId);

    Optional<Push> findByDeviceId(String deviceId);

    List<Push> findAllByUserId(Long userId);
}
