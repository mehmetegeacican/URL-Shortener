package com.urlshortenerserver.server.controller;

import com.urlshortenerserver.server.dto.UrlDto;
import com.urlshortenerserver.server.dto.converter.UrlDtoConverter;
import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.request.UrlRequest;
import com.urlshortenerserver.server.request.converter.UrlRequestConverter;
import com.urlshortenerserver.server.request.filter.UrlFilter;
import com.urlshortenerserver.server.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v2")
@Tag(name = "URLs", description = "Create, look up, resolve and delete short URLs")
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

    @Operation(summary = "List URLs", description = "Returns all short URLs, optionally filtered.")
    @GetMapping("/all")
    public ResponseEntity<List<UrlDto>> getAllUrls(@ParameterObject @ModelAttribute UrlFilter filter){
        return new ResponseEntity<List<UrlDto>>(
                urlDtoConverter.convertToDto(service.getAllUrls(filter)), HttpStatus.OK
        );
    }

    @Operation(summary = "Get URL details", description = "Returns the details of a short URL by its code.")
    @GetMapping("/show/{code}")
    public ResponseEntity<UrlDto> getUrlByCode(
            @Parameter(description = "Short code of the URL", example = "aB3dE")
            @Valid @NotNull @PathVariable String code) throws Exception {
        return new ResponseEntity<UrlDto>(
                urlDtoConverter.convertToDto(service.getUrlByCode(code)), HttpStatus.OK
        );
    }

    @Operation(summary = "Redirect to the original URL", description = "Resolves the short code, records the click and redirects to the original URL.")
    @ApiResponse(responseCode = "303", description = "Redirect to the original URL")
    @GetMapping("/{code}")
    public ResponseEntity<List<UrlDto>> redirect(
            @Parameter(description = "Short code of the URL", example = "aB3dE")
            @Valid @NotNull @PathVariable String code, HttpServletRequest request) throws Exception{

        // 1. Get the target URL (hits cache or DB safely)
        Url url = service.getUrlByCode(code);

        // 2. Log the click event independently
        service.recordClick(code, request);

        URI uri = new URI(url.getUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);

        return new ResponseEntity<>(
                httpHeaders,HttpStatus.SEE_OTHER
        );
    }



    @Operation(summary = "Create a short URL", description = "Creates a new short URL for the given target.")
    @ApiResponse(responseCode = "201", description = "Short URL created")
    @PostMapping("/create")
    public ResponseEntity<UrlDto> postURL (@Valid @RequestBody UrlRequest urlRequest){
        Url url = urlRequestConverter.convertToEntity(urlRequest);


        // Check if ID is null, and if so, generate a new ID
        if (url.getId() == null) {
            url.setId(service.generateID()); // Replace with your custom ID generation logic
        }

        // Save the URL entity
        Url createdUrl = service.create(url);

        // Convert the created URL entity to DTO
        UrlDto urlDto = urlDtoConverter.convertToDto(createdUrl);

        return new ResponseEntity<>(urlDto, HttpStatus.CREATED);
    }


    @Operation(summary = "Delete a short URL", description = "Deletes the short URL with the given code.")
    @ApiResponse(responseCode = "204", description = "URL deleted")
    @DeleteMapping("/delete/{code}")
    public ResponseEntity<Void> deleteUrl(
            @Parameter(description = "Short code of the URL", example = "aB3dE")
            @Valid @NotNull @PathVariable String code) throws Exception {
        service.deleteUrl(code);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}