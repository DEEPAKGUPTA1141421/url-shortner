package com.example.URLSHORTNER.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "url_mappings")
@Getter
@Setter
public class UrlMapping {
    @Id
    private Long id;

    @Column(name = "short_code", nullable = false, length = 20)
    private String shortCode;

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(name = "url_hash", nullable = false, length = 64)
    private String urlHash;

    @Column(name = "is_custom_alias", nullable = false)
    private boolean customAlias;

    @Column(name = "click_count", nullable = false)
    private long clickCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    protected UrlMapping() {
        // JPA
    }

    public UrlMapping(Long id, String shortCode, String originalUrl, String urlHash, boolean customAlias) {
        this.id = id;
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.urlHash = urlHash;
        this.customAlias = customAlias;
        this.clickCount = 0L;
        this.createdAt = Instant.now();
    }

    public void recordVisit() {
        this.clickCount++;
        this.lastAccessedAt = Instant.now();
    }
}
