package com.digital.menu.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.digital.menu.dto.MediaUploadResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
class S3MediaStorageServiceTest {

    @Mock
    private S3Client s3Client;

    private S3MediaStorageService service;

    @BeforeEach
    void setUp() {
        service = new S3MediaStorageService(s3Client);
        ReflectionTestUtils.setField(service, "enabled", true);
        ReflectionTestUtils.setField(service, "bucket", "demo-bucket");
        ReflectionTestUtils.setField(service, "region", "ap-south-1");
        ReflectionTestUtils.setField(service, "keyPrefix", "menu-media");
        ReflectionTestUtils.setField(service, "publicBaseUrl", "");
        ReflectionTestUtils.setField(service, "maxFileSizeBytes", 15_728_640L);
        ReflectionTestUtils.setField(service, "allowedImageContentTypes", java.util.List.of("image/jpeg", "image/png"));
        ReflectionTestUtils.setField(service, "allowedVideoContentTypes", java.util.List.of("video/mp4", "video/webm"));
    }

    @Test
    void upload_shouldUploadImageAndReturnPublicUrl() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "dish.jpg",
            "image/jpeg",
            "sample-image".getBytes()
        );

        MediaUploadResponse response = service.upload("tenant-a", "image", file);

        assertNotNull(response.getKey());
        assertTrue(response.getKey().startsWith("menu-media/tenant-a/image/"));
        assertTrue(response.getUrl().startsWith("https://demo-bucket.s3.ap-south-1.amazonaws.com/menu-media/tenant-a/image/"));
        assertEquals("image/jpeg", response.getContentType());
        assertEquals("image", response.getMediaType());

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        assertEquals("demo-bucket", requestCaptor.getValue().bucket());
        assertEquals("image/jpeg", requestCaptor.getValue().contentType());
    }

    @Test
    void upload_shouldRejectUnsupportedContentType() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "clip.mov",
            "video/quicktime",
            "video".getBytes()
        );

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.upload("tenant-a", "video", file)
        );

        assertTrue(ex.getMessage().contains("Unsupported content type"));
    }

    @Test
    void upload_shouldRejectWhenDisabled() {
        ReflectionTestUtils.setField(service, "enabled", false);
        MockMultipartFile file = new MockMultipartFile("file", "dish.jpg", "image/jpeg", "x".getBytes());

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.upload("tenant-a", "image", file)
        );

        assertEquals("S3 upload is disabled. Set app.s3.enabled=true.", ex.getMessage());
    }
}
