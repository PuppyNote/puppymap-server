package com.puppymapserver.storage.controller;

import com.puppymapserver.global.ApiResponse;
import com.puppymapserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/storage")
public class StorageController {

    private final S3StorageService s3StorageService;

    @PostMapping
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(s3StorageService.upload(file, S3StorageService.PLACE_FOLDER));
    }
}
