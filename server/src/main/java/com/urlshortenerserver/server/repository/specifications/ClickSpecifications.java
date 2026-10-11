package com.urlshortenerserver.server.repository.specifications;

import com.urlshortenerserver.server.model.Click;
import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.repository.ClickRepository;
import com.urlshortenerserver.server.repository.UrlRepository;
import com.urlshortenerserver.server.response.AdminStatResponse;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClickSpecifications {

    public static Specification<Click> withFilters(String code, Instant from, Instant to) {
        return Specification.where(hasCode(code))
                .and(clickedBetween(from, to));
    }
    private static Specification<Click> hasCode(String code) {
        return (root, query, cb) -> cb.equal(root.get("code"), code);
    }
    private static Specification<Click> clickedBetween(Instant from, Instant to) {
        return (root, query, cb) -> {
            if (from == null && to == null) {
                return null;
            }
            if (from != null && to != null) {
                return cb.between(root.get("clickedAt"), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("clickedAt"), from);
            }
            return cb.lessThanOrEqualTo(root.get("clickedAt"), to);
        };
    }

    public static List<AdminStatResponse.TopIpDto> getTopIps(ClickRepository clickRepository, int limit) {
        List<Click> allClicks = clickRepository.findAll();
        return allClicks.stream()
                .filter(c -> c.getIp() != null)
                .collect(Collectors.groupingBy(Click::getIp, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> new AdminStatResponse.TopIpDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
    public static List<AdminStatResponse.TopUrlDto> getTopUrls(ClickRepository clickRepository, UrlRepository urlRepository, int limit) {
        List<Click> allClicks = clickRepository.findAll();
        Map<String, String> codeToUrlMap = urlRepository.findAll().stream()
                .collect(Collectors.toMap(Url::getCode, Url::getUrl, (u1, u2) -> u1));

        return allClicks.stream()
                .filter(c -> c.getCode() != null)
                .collect(Collectors.groupingBy(Click::getCode, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> new AdminStatResponse.TopUrlDto(
                        entry.getKey(),
                        codeToUrlMap.getOrDefault(entry.getKey(), ""),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
    }

    public static List<AdminStatResponse.ClicksPerDayDto> getClicksPerDay(ClickRepository clickRepository, int limit) {
        List<Click> allClicks = clickRepository.findAll();
        return allClicks.stream()
                .filter(c -> c.getClickedAt() != null)
                .collect(Collectors.groupingBy(
                        c -> LocalDateTime.ofInstant(c.getClickedAt(), ZoneId.systemDefault()).toLocalDate(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<LocalDate, Long>comparingByKey().reversed())
                .limit(limit)
                .map(entry -> new AdminStatResponse.ClicksPerDayDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
