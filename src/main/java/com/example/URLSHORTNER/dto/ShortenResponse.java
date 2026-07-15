package com.example.URLSHORTNER.dto;

import java.time.Instant;

import com.example.URLSHORTNER.entity.UrlMapping;

public class ShortenResponse {
    private final String shortCode;
    private final String shortUrl;
    private final String originalUrl;
    private final Instant createdAt;

    public ShortenResponse(UrlMapping mapping, String baseUrl) {
        this.shortCode = mapping.getShortCode();
        this.shortUrl = baseUrl + "/" + mapping.getShortCode();
        this.originalUrl = mapping.getOriginalUrl();
        this.createdAt = mapping.getCreatedAt();
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
