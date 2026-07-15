package com.example.URLSHORTNER.controller;

import com.example.URLSHORTNER.dto.ShortenRequest;
import com.example.URLSHORTNER.entity.UrlMapping;
import com.example.URLSHORTNER.exception.AliasAlreadyTakenException;
import com.example.URLSHORTNER.exception.InvalidUrlException;
import com.example.URLSHORTNER.exception.ShortCodeNotFoundException;
import com.example.URLSHORTNER.service.UrlShortenerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@TestPropertySource(properties = "app.base-url=http://short.test")
class UrlControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @Test
    void shortenThenRedirect_roundTripsCorrectly() throws Exception {
        ShortenRequest request = new ShortenRequest();
        request.setUrl("https://dashly.example/orders/42");

        UrlMapping mapping = new UrlMapping(1001L, "abc123", "https://dashly.example/orders/42", "hash", false);
        given(urlShortenerService.shorten("https://dashly.example/orders/42", null)).willReturn(mapping);

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.shortUrl").value("http://short.test/abc123"));

        given(urlShortenerService.resolve("abc123")).willReturn(mapping);

        mockMvc.perform(get("/abc123"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "https://dashly.example/orders/42"));
    }

    @Test
    void unknownCodeReturns404() throws Exception {
        given(urlShortenerService.resolve("doesNotExist"))
                .willThrow(new ShortCodeNotFoundException("doesNotExist"));

        mockMvc.perform(get("/doesNotExist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sameUrlShortenedTwiceReturnsSameCode() throws Exception {
        ShortenRequest request = new ShortenRequest();
        request.setUrl("https://dashly.example/dup-test");

        UrlMapping mapping = new UrlMapping(1001L, "dup123", "https://dashly.example/dup-test", "hash", false);
        given(urlShortenerService.shorten("https://dashly.example/dup-test", null)).willReturn(mapping);

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void customAliasConflictReturns409() throws Exception {
        ShortenRequest request = new ShortenRequest();
        request.setUrl("https://dashly.example/a");
        request.setCustomAlias("promo25");

        given(urlShortenerService.shorten("https://dashly.example/a", "promo25"))
                .willThrow(new AliasAlreadyTakenException("promo25"));

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidUrlReturns400() throws Exception {
        ShortenRequest request = new ShortenRequest();
        request.setUrl("not-a-url");

        given(urlShortenerService.shorten("not-a-url", null))
                .willThrow(new InvalidUrlException("Malformed URL: not-a-url"));

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
