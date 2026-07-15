package com.example.URLSHORTNER.service;

import com.example.URLSHORTNER.entity.UrlMapping;
import com.example.URLSHORTNER.exception.AliasAlreadyTakenException;
import com.example.URLSHORTNER.exception.InvalidUrlException;
import com.example.URLSHORTNER.exception.ShortCodeNotFoundException;
import com.example.URLSHORTNER.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository repository;

    private UrlShortenerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UrlShortenerService(repository);
    }

    @Test
    void shortensANewUrlWithGeneratedCode() {
        when(repository.findByUrlHashAndCustomAliasFalse(anyString())).thenReturn(Optional.empty());
        when(repository.nextId()).thenReturn(1000L);
        when(repository.save(any(UrlMapping.class))).thenAnswer(inv -> inv.getArgument(0));

        UrlMapping result = service.shorten("https://dashly.example/orders/123", null);

        assertEquals("https://dashly.example/orders/123", result.getOriginalUrl());
        assertFalse(result.isCustomAlias());
        assertEquals(Base62Encoder.encode(1000L), result.getShortCode());
        verify(repository).save(any(UrlMapping.class));
    }

    @Test
    void sameUrlSubmittedTwiceReturnsExistingMapping_idempotentByDesign() {
        UrlMapping existing = new UrlMapping(1000L, "abc1234", "https://dashly.example/x", "hash", false);
        when(repository.findByUrlHashAndCustomAliasFalse(anyString())).thenReturn(Optional.of(existing));

        UrlMapping result = service.shorten("https://dashly.example/x", null);

        assertSame(existing, result);
        verify(repository, never()).nextId();
        verify(repository, never()).save(any());
    }

    @Test
    void customAliasCreatesNewMappingEvenIfUrlAlreadyShortened() {
        // Even though the URL already has a system-generated code, an explicit
        // custom alias request should still create a distinct row.
        when(repository.existsByShortCode("myproduct")).thenReturn(false);
        when(repository.nextId()).thenReturn(2000L);
        when(repository.save(any(UrlMapping.class))).thenAnswer(inv -> inv.getArgument(0));

        UrlMapping result = service.shorten("https://dashly.example/x", "myproduct");

        assertEquals("myproduct", result.getShortCode());
        assertTrue(result.isCustomAlias());
        verify(repository, never()).findByUrlHashAndCustomAliasFalse(anyString());
    }

    @Test
    void rejectsCustomAliasAlreadyInUse() {
        when(repository.existsByShortCode("taken")).thenReturn(true);

        assertThrows(AliasAlreadyTakenException.class,
                () -> service.shorten("https://dashly.example/y", "taken"));

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsNonHttpScheme() {
        assertThrows(InvalidUrlException.class,
                () -> service.shorten("ftp://dashly.example/file", null));
    }

    @Test
    void rejectsMalformedUrl() {
        assertThrows(InvalidUrlException.class,
                () -> service.shorten("not a url", null));
    }

    @Test
    void rejectsBlankUrl() {
        assertThrows(InvalidUrlException.class, () -> service.shorten("   ", null));
    }

    @Test
    void resolveIncrementsClickCountAndReturnsMapping() {
        UrlMapping mapping = new UrlMapping(1000L, "abc1234", "https://dashly.example/x", "hash", false);
        when(repository.findByShortCode("abc1234")).thenReturn(Optional.of(mapping));
        when(repository.save(any(UrlMapping.class))).thenAnswer(inv -> inv.getArgument(0));

        UrlMapping result = service.resolve("abc1234");

        assertEquals(1, result.getClickCount());
        assertNotNull(result.getLastAccessedAt());
    }

    @Test
    void resolveThrowsNotFoundForUnknownCode() {
        when(repository.findByShortCode("ghost99")).thenReturn(Optional.empty());

        assertThrows(ShortCodeNotFoundException.class, () -> service.resolve("ghost99"));
    }
}
