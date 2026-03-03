package com.digital.menu.dto;

public class MediaUploadResponse {
    private final String key;
    private final String url;
    private final String contentType;
    private final long sizeBytes;
    private final String mediaType;

    public MediaUploadResponse(String key, String url, String contentType, long sizeBytes, String mediaType) {
        this.key = key;
        this.url = url;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.mediaType = mediaType;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public String getMediaType() {
        return mediaType;
    }
}
