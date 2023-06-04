package com.urlshortenerserver.server.request.converter;

import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.request.UrlRequest;
import org.springframework.stereotype.Component;

@Component
public class UrlRequestConverter {

    public Url convertToEntity(UrlRequest urlRequest){
        return Url.builder().url(urlRequest.getUrl()).code(urlRequest.getCode()).build();
    }
}
