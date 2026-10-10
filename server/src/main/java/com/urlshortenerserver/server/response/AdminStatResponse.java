package com.urlshortenerserver.server.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminStatResponse {
    private UrlStats urls;
    private UserStats users;
    private ClickStats clicks;
    private List<TopUrlDto> topUrls;
    private List<TopIpDto> topIps;
    private List<ClicksPerDayDto> clicksPerDay;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UrlStats {
        private long total;
        private long active;
        private long deleted;
        private long anonymous;
        private long createdToday;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserStats {
        private long total;
        private long admins;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ClickStats {
        private long total;
        private long today;
        private long uniqueIps;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TopUrlDto {
        private String code;
        private String url;
        private long clicks;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TopIpDto {
        private String ip;
        private long clicks;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ClicksPerDayDto {
        private LocalDate date;
        private long clicks;
    }
}