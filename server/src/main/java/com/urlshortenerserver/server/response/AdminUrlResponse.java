package com.urlshortenerserver.server.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdminUrlResponse {
    String code;
    String url;
    boolean deleted;
    UUID ownerId;
    String ownerUsername;
    Instant createdAt;
    long clickCount;
    Instant lastClickedAt;
}
