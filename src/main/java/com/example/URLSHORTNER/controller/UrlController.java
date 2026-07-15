package com.example.URLSHORTNER.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.URLSHORTNER.dto.ShortenRequest;
import com.example.URLSHORTNER.dto.ShortenResponse;
import com.example.URLSHORTNER.entity.UrlMapping;
import com.example.URLSHORTNER.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlShortenerService service;

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("URL Shortener is running");
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        UrlMapping mapping = service.shorten(request.getUrl(), request.getCustomAlias());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ShortenResponse(mapping, baseUrl));
    }
}