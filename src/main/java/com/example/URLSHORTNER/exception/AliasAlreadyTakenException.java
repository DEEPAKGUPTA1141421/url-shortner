package com.example.URLSHORTNER.exception;

public class AliasAlreadyTakenException extends RuntimeException {
    public AliasAlreadyTakenException(String alias) {
        super("Alias '" + alias + "' is already in use");
    }
}
