package com.puppymapserver.user.users.service;

import com.puppymapserver.user.users.service.request.EmailSendServiceRequest;
import com.puppymapserver.user.users.service.request.SignUpServiceRequest;
import com.puppymapserver.user.users.service.request.UserProfileUpdateServiceRequest;
import com.puppymapserver.user.users.service.response.SignUpResponse;

public interface UserService {
    SignUpResponse signUp(SignUpServiceRequest request);
    String sendVerificationEmail(EmailSendServiceRequest request);
    void updateProfile(UserProfileUpdateServiceRequest request);
    void withdraw();
}
