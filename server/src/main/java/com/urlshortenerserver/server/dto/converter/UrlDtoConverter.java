package com.urlshortenerserver.server.dto.converter;

import com.urlshortenerserver.server.dto.UrlDto;
import com.urlshortenerserver.server.model.Url;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UrlDtoConverter {

    /**
     * Converts a Model to DTO, uses the builder pattern
     * @param urlParam
     * @return
     */
    public UrlDto convertToDto(Url urlParam){
        return UrlDto.builder().
                id(urlParam.getId()).
                url(urlParam.getUrl()).
                code(urlParam.getCode()).
                build();
    }

    public List<UrlDto> convertToDto(List<Url> urlList) {
        return urlList.stream()
                .map(this::convertToDto).collect(Collectors.toList());
    }
}
