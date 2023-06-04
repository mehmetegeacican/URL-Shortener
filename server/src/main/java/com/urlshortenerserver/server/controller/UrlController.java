package com.urlshortenerserver.server.controller;

import com.urlshortenerserver.server.dto.UrlDto;
import com.urlshortenerserver.server.dto.converter.UrlDtoConverter;
import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.request.UrlRequest;
import com.urlshortenerserver.server.request.converter.UrlRequestConverter;
import com.urlshortenerserver.server.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/")
public class UrlController {

    private final UrlDtoConverter urlDtoConverter;
    private final UrlRequestConverter urlRequestConverter;

    private final UrlService service;

    /**
     * Constructor Based Injection
     *
     * @param urlDtoConverter
     * @param urlRequestConverter
     * @param service
     */
    public UrlController(UrlDtoConverter urlDtoConverter, UrlRequestConverter urlRequestConverter, UrlService service) {
        this.urlDtoConverter = urlDtoConverter;
        this.urlRequestConverter = urlRequestConverter;
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<UrlDto>> getAllUrls(){
        return new ResponseEntity<List<UrlDto>>(
                urlDtoConverter.convertToDto(service.getAllUrls()), HttpStatus.OK
        );
    }

    @GetMapping("/show/{code}")
    public ResponseEntity<UrlDto> getUrlByCode(@Valid @NotNull @PathVariable String code) throws Exception {
        return new ResponseEntity<UrlDto>(
                urlDtoConverter.convertToDto(service.getUrlByCode(code)), HttpStatus.OK
        );
    }

    @GetMapping("/{code}")
    public ResponseEntity<List<UrlDto>> redirect(@Valid @NotNull @PathVariable String code) throws Exception{

        Url url = service.getUrlByCode(code);

        URI uri = new URI(url.getUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);

        return new ResponseEntity<>(
               httpHeaders,HttpStatus.SEE_OTHER
        );
    }

    @PostMapping
    public ResponseEntity<?> postURL (@Valid @RequestBody UrlRequest urlRequest){
        Url convertedUrl = urlRequestConverter.convertToEntity((urlRequest));
        return new ResponseEntity<UrlDto>(
                urlDtoConverter.convertToDto(service.createUrl(convertedUrl)), HttpStatus.CREATED
        );
    }
}
