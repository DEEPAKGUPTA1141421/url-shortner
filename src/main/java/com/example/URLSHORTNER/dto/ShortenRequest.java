package com.example.URLSHORTNER.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ShortenRequest {

    @NotBlank(message = "url must not be blank")
    private String url;

    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,20}$", message = "customAlias must be 3-20 chars: letters, digits, '-' or '_'")
    @Size(max = 20)
    private String customAlias;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }
}
