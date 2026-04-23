package com.puppymapserver.global.email;

public interface EmailService {
    Long sendVerificationCode(String email);
    boolean verifyCode(Long verificationId, String code);
}
