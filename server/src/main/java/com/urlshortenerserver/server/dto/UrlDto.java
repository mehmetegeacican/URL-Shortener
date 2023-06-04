package com.urlshortenerserver.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
/**
 * Data Transfter Object for Url
 */
public class UrlDto {
    private Long id;
    private String url;
    private String code;
}
