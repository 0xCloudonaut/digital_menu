package com.digital.menu.service;

import com.digital.menu.dto.MediaUploadResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3MediaStorageService {
    private static final Set<String> ALLOWED_MEDIA_TYPES = Set.of("image", "video");
    private static final Map<String, String> EXTENSIONS = Map.of(
        "image/jpeg", "jpg",
        "image/png", "png",
        "image/webp", "webp",
        "image/gif", "gif",
        "video/mp4", "mp4",
        "video/webm", "webm",
        "video/quicktime", "mov"
    );

    private final S3Client s3Client;

    @Value("${app.s3.enabled:false}")
    private boolean enabled;

    @Value("${app.s3.bucket:}")
    private String bucket;

    @Value("${app.s3.region:}")
    private String region;

    @Value("${app.s3.key-prefix:menu-media}")
    private String keyPrefix;

    @Value("${app.s3.public-base-url:}")
    private String publicBaseUrl;

    @Value("${app.s3.max-file-size-bytes:15728640}")
    private long maxFileSizeBytes;

    @Value("${app.s3.allowed-image-content-types:image/jpeg,image/png,image/webp,image/gif}")
    private List<String> allowedImageContentTypes;

    @Value("${app.s3.allowed-video-content-types:video/mp4,video/webm,video/quicktime}")
    private List<String> allowedVideoContentTypes;

    public S3MediaStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public MediaUploadResponse upload(String tenantId, String mediaType, MultipartFile file) {
        if (!enabled) {
            throw new IllegalArgumentException("S3 upload is disabled. Set app.s3.enabled=true.");
        }
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalArgumentException("app.s3.bucket must be configured for uploads");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId is required");
        }
        String normalizedMediaType = normalizeMediaType(mediaType);
        validateFile(file, normalizedMediaType);

        String contentType = file.getContentType().toLowerCase(Locale.ROOT);
        String extension = EXTENSIONS.getOrDefault(contentType, extensionFromFilename(file.getOriginalFilename()));
        String key = buildKey(tenantId, normalizedMediaType, extension);

        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .contentLength(file.getSize())
            .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read uploaded file", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to upload file to S3", ex);
        }

        String url = buildPublicUrl(key);
        return new MediaUploadResponse(key, url, contentType, file.getSize(), normalizedMediaType);
    }

    private String normalizeMediaType(String mediaType) {
        String normalized = mediaType == null ? "" : mediaType.trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_MEDIA_TYPES.contains(normalized)) {
            throw new IllegalArgumentException("mediaType must be either image or video");
        }
        return normalized;
    }

    private void validateFile(MultipartFile file, String mediaType) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new IllegalArgumentException("file exceeds max size of " + maxFileSizeBytes + " bytes");
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("file content type is required");
        }
        String normalized = contentType.toLowerCase(Locale.ROOT);
        List<String> allowed = "image".equals(mediaType) ? allowedImageContentTypes : allowedVideoContentTypes;
        if (!allowed.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }
    }

    private String buildKey(String tenantId, String mediaType, String extension) {
        LocalDate today = LocalDate.now();
        return String.format(
            "%s/%s/%s/%d/%02d/%02d/%s.%s",
            sanitizePathSegment(keyPrefix),
            sanitizePathSegment(tenantId),
            mediaType,
            today.getYear(),
            today.getMonthValue(),
            today.getDayOfMonth(),
            UUID.randomUUID(),
            sanitizePathSegment(extension)
        );
    }

    private String buildPublicUrl(String key) {
        String base = (publicBaseUrl == null || publicBaseUrl.isBlank())
            ? String.format("https://%s.s3.%s.amazonaws.com", bucket, region)
            : publicBaseUrl;
        String normalizedBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        return normalizedBase + "/" + key;
    }

    private String extensionFromFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "bin";
        }
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            return "bin";
        }
        return filename.substring(idx + 1).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private String sanitizePathSegment(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/_-]", "-");
    }
}
