package com.puppymapserver.storage.controller;

import com.puppymapserver.global.ApiResponse;
import com.puppymapserver.storage.enums.BucketKind;
import com.puppymapserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/storage")
public class StorageController {

    private final S3StorageService s3StorageService;

    /**
     * S3에 파일 업로드 (영구 URL 반환)
     */
    @PostMapping("/{bucketKind}")
    public ApiResponse<String> upload(
            @RequestParam("file") MultipartFile file, @PathVariable("bucketKind") BucketKind bucketKind) {
        return ApiResponse.ok(s3StorageService.upload(file, bucketKind));
    }

}
