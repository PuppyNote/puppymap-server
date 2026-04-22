package com.puppymapserver.storage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService {

    static final String PLACE_FOLDER = "place";
    static final String USER_PROFILE_FOLDER = "user-profile";

    private final S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    public String upload(MultipartFile file) {
        validateImageFile(file);
        String imageKey = generateObjectKey(file.getOriginalFilename());
        String objectKey = "place/" + imageKey;
        uploadToS3(file, objectKey);
        return imageKey;
    }

    public String getCloudFrontUrl(String objectKey, String folder) {
        if (objectKey == null || objectKey.isEmpty()) {
            return objectKey;
        }
        String key = objectKey.startsWith(folder + "/")
                ? objectKey
                : folder + "/" + objectKey;
        return cloudFrontDomain + "/" + key;
    }

    public void deleteObject(String imageKey, String folder) {
        if (imageKey == null || imageKey.isEmpty()) return;
        String objectKey = imageKey.startsWith(folder + "/")
                ? imageKey
                : folder + "/" + imageKey;
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build());
            log.info("S3 파일 삭제 성공: {}", objectKey);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}, message: {}", objectKey, e.getMessage());
        }
    }

    public void deleteObjects(List<String> imageKeys, String folder) {
        if (imageKeys == null || imageKeys.isEmpty()) return;
        imageKeys.forEach(key -> deleteObject(key, folder));
    }

    private void uploadToS3(MultipartFile file, String objectKey) {
        validateFile(file);
        try {
            String contentType = determineContentType(file);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();

            PutObjectResponse response = s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            log.info("S3 파일 업로드 성공: {}, ETag: {}", objectKey, response.eTag());

        } catch (IOException e) {
            log.error("파일 업로드 중 IO 오류 발생: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("파일 업로드 실패", e);
        } catch (Exception e) {
            log.error("S3 파일 업로드 중 오류 발생: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
    }

    private void validateImageFile(MultipartFile file) {
        validateFile(file);
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("video/")) {
            throw new IllegalArgumentException("동영상 파일은 업로드할 수 없습니다.");
        }
    }

    private String generateObjectKey(String originalFilename) {
        String filename = UUID.randomUUID().toString();
        String extension = getFileExtension(originalFilename);
        if (!filename.endsWith(extension)) {
            filename += extension;
        }
        return filename;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) return "";
        return filename.substring(lastDotIndex).toLowerCase();
    }

    private String determineContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isEmpty()) return contentType;
        return switch (getFileExtension(file.getOriginalFilename())) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            case ".pdf" -> "application/pdf";
            case ".txt" -> "text/plain";
            case ".json" -> "application/json";
            default -> "application/octet-stream";
        };
    }
}
