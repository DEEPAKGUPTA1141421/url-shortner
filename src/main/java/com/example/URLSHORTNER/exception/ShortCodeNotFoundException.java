package com.example.URLSHORTNER.exception;

public class ShortCodeNotFoundException extends RuntimeException {
    public ShortCodeNotFoundException(String code) {
        super("No URL found for code '" + code + "'");
    }
}
