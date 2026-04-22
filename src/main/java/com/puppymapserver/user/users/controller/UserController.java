package com.puppymapserver.user.users.controller;

import com.puppymapserver.global.ApiResponse;
import com.puppymapserver.user.users.controller.request.EmailSendRequest;
import com.puppymapserver.user.users.controller.request.SignUpRequest;
import com.puppymapserver.user.users.service.UserService;
import com.puppymapserver.user.users.service.response.SignUpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public ApiResponse<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return ApiResponse.created(userService.signUp(request.toServiceRequest()));
    }

    @PostMapping("/email/send")
    public ApiResponse<String> sendVerificationEmail(@Valid @RequestBody EmailSendRequest request) {
        return ApiResponse.ok(userService.sendVerificationEmail(request.toServiceRequest()));
    }

    @DeleteMapping("/withdraw")
    public ApiResponse<Void> withdraw() {
        userService.withdraw();
        return ApiResponse.ok(null);
    }
}
