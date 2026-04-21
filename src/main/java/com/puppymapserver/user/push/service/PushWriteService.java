package com.puppymapserver.user.push.service;

import com.puppymapserver.user.users.entity.User;

public interface PushWriteService {

    void upsertByDeviceId(String deviceId, User user, String pushToken);
}
