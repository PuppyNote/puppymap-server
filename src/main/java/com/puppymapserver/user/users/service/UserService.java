package com.puppymapserver.user.users.service;

import com.puppymapserver.user.users.service.request.EmailSendServiceRequest;
import com.puppymapserver.user.users.service.request.EmailVerifyServiceRequest;
import com.puppymapserver.user.users.service.request.SignUpServiceRequest;
import com.puppymapserver.user.users.service.response.SignUpResponse;

public interface UserService {
    SignUpResponse signUp(SignUpServiceRequest request);
    Long sendVerificationEmail(EmailSendServiceRequest request);
    boolean verifyEmail(EmailVerifyServiceRequest request);
    void withdraw();
}
