package com.urlshortenerserver.server.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UrlClickStatResponse {
    private String code;

    // Summary metrics
    private long totalClicks;
    private long uniqueIps;
    private Instant firstClick;
    private Instant lastClick;
    private List<TopIp> topIps;

    // Paginated click logs
    private PageResponse<ClickLog> clicks;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TopIp {
        private String ip;
        private long clicks;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ClickLog {
        private String ip;
        private String userAgent;
        private String referer;
        private Instant clickedAt;
    }
}