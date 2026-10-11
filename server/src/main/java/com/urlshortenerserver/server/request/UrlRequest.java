package com.urlshortenerserver.server.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UrlRequest {
    @NotNull
    private String url;

    @Pattern(
            regexp = "^[A-Za-z0-9_-]{4,20}$",
            message = "Code must be 4-20 characters: letters, numbers, '-' or '_'"
    )
    private String code;

    private UUID userId;

}
