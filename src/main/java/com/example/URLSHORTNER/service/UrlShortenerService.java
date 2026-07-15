package com.example.URLSHORTNER.service;

import com.example.URLSHORTNER.entity.UrlMapping;
import com.example.URLSHORTNER.exception.AliasAlreadyTakenException;
import com.example.URLSHORTNER.exception.InvalidUrlException;
import com.example.URLSHORTNER.exception.ShortCodeNotFoundException;
import com.example.URLSHORTNER.repository.UrlMappingRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UrlShortenerService {
    private final UrlMappingRepository repository;

    @Transactional
    public UrlMapping shorten(String rawUrl, String customAlias) {
        String normalized = validateAndNormalize(rawUrl);
        String hash = sha256(normalized);

        if (customAlias != null && !customAlias.isBlank()) {
            return createWithCustomAlias(normalized, hash, customAlias);
        }

        return findExisting(hash).orElseGet(() -> createWithGeneratedCode(normalized, hash));
    }

    public UrlMapping resolve(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));
        mapping.recordVisit();
        return repository.save(mapping);
    }

    public UrlMapping getAnalytics(String shortCode) {
        return repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException(shortCode));
    }

    private Optional<UrlMapping> findExisting(String hash) {
        return repository.findByUrlHashAndCustomAliasFalse(hash);
    }

    private UrlMapping createWithGeneratedCode(String normalizedUrl, String hash) {
        try {
            Long id = repository.nextId();
            String code = Base62Encoder.encode(id);
            UrlMapping mapping = new UrlMapping(id, code, normalizedUrl, hash, false);
            return repository.save(mapping);
        } catch (DataIntegrityViolationException e) {
            // Race: another request inserted the same URL between our lookup and
            // insert. Re-read and return the winner rather than failing the request.
            return findExisting(hash)
                    .orElseThrow(() -> e);
        }
    }

    private UrlMapping createWithCustomAlias(String normalizedUrl, String hash, String customAlias) {
        if (repository.existsByShortCode(customAlias)) {
            throw new AliasAlreadyTakenException(customAlias);
        }
        try {
            Long id = repository.nextId();
            UrlMapping mapping = new UrlMapping(id, customAlias, normalizedUrl, hash, true);
            return repository.save(mapping);
        } catch (DataIntegrityViolationException e) {
            // Race: someone else grabbed the same alias between our check and insert.
            throw new AliasAlreadyTakenException(customAlias);
        }
    }

    private String validateAndNormalize(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new InvalidUrlException("url must not be blank");
        }
        String trimmed = rawUrl.trim();
        try {
            URL url = new URL(trimmed);
            String protocol = url.getProtocol();
            if (!"http".equals(protocol) && !"https".equals(protocol)) {
                throw new InvalidUrlException("Only http/https URLs are supported");
            }
            if (url.getHost() == null || url.getHost().isBlank()) {
                throw new InvalidUrlException("URL must include a host");
            }
        } catch (MalformedURLException e) {
            throw new InvalidUrlException("Malformed URL: " + e.getMessage());
        }
        return trimmed;
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
