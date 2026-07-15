package com.example.URLSHORTNER.dto;

import java.time.Instant;

import com.example.URLSHORTNER.entity.UrlMapping;

public class AnalyticsResponse {
    private final String shortCode;
    private final String originalUrl;
    private final long clickCount;
    private final Instant createdAt;
    private final Instant lastAccessedAt;

    public AnalyticsResponse(UrlMapping mapping) {
        this.shortCode = mapping.getShortCode();
        this.originalUrl = mapping.getOriginalUrl();
        this.clickCount = mapping.getClickCount();
        this.createdAt = mapping.getCreatedAt();
        this.lastAccessedAt = mapping.getLastAccessedAt();
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public long getClickCount() {
        return clickCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastAccessedAt() {
        return lastAccessedAt;
    }
}
