package com.urlshortenerserver.server.exception;

import com.urlshortenerserver.server.service.UrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UrlNotFoundException extends RuntimeException{

    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);
    public UrlNotFoundException(String message){
        super(message);
        logger.debug("URL not found: {} :", message);
    }
}
