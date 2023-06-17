package com.urlshortenerserver.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortenerserver.server.dto.UrlDto;
import com.urlshortenerserver.server.dto.converter.UrlDtoConverter;
import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.repository.UrlRepository;
import com.urlshortenerserver.server.request.UrlRequest;
import com.urlshortenerserver.server.request.converter.UrlRequestConverter;
import com.urlshortenerserver.server.service.UrlService;
import netscape.javascript.JSObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoPostProcessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(UrlController.class)
@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UrlDtoConverter dtoConverter;

    @MockBean
    private UrlRequestConverter requestConverter;

    private UrlController urlController;


    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        urlController = new UrlController(dtoConverter,requestConverter,urlService);
    }

    @Test
    void getAllUrls() throws Exception {
        //Given
        Url url1 = new Url(1l,"http://helloThere.com","test1");
        Url url2 = new Url(2l,"http://hellorThere.com","test2");
        List<Url> testList = new ArrayList<>(Arrays.asList(url1,url2));
        List<UrlDto> expectedDtoList = new ArrayList<>();
        expectedDtoList.add(dtoConverter.convertToDto(url1));
        expectedDtoList.add(dtoConverter.convertToDto(url2));
        //When
        Mockito.when(urlService.getAllUrls()).thenReturn(testList);
        Mockito.when(dtoConverter.convertToDto(testList)).thenReturn(expectedDtoList);
        ResponseEntity<List<UrlDto>> response = urlController.getAllUrls();
        //Then
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(expectedDtoList,response.getBody());

        Mockito.verify(urlService,Mockito.times(1)).getAllUrls();
        Mockito.verify(dtoConverter,Mockito.times(1)).convertToDto(testList);

    }

    @Test
    void getUrlByCode() throws Exception {
        //Given
        Url url1 = new Url(1l,"http://helloThere.com","test1");
        UrlDto urlDto = dtoConverter.convertToDto(url1);
        //When
        Mockito.when(urlService.getUrlByCode("test1")).thenReturn(url1);

        //Then
        ResponseEntity<UrlDto> response = urlController.getUrlByCode("test1");
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(urlDto,response.getBody());
    }

    @Test
    void redirect() throws Exception {
        //Given
        String code = "test";
        Url url = new Url(1l,"http://example.com","test");
        URI uri = new URI(url.getUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        //When
        Mockito.when(urlService.getUrlByCode(code)).thenReturn(url);
        //Then
        ResponseEntity<List<UrlDto>> response = urlController.redirect(code);
        assertEquals(HttpStatus.SEE_OTHER,response.getStatusCode());
        assertEquals(httpHeaders,response.getHeaders());

    }

    @Test
    void postURL() throws Exception {
        //Given
        UrlRequest request = UrlRequest.builder().url("http://example.com").build();
        Url createdUrl = new Url(null,"http://example.com","test");
        UrlDto createdUrlDto = new UrlDto(2l,"http://example.com","test");
        //When
        Mockito.when(requestConverter.convertToEntity(request)).thenReturn(createdUrl);
        Mockito.when(urlService.generateCode()).thenReturn("test");
        Mockito.when(urlService.create(createdUrl)).thenReturn(createdUrl);
        Mockito.when(dtoConverter.convertToDto(createdUrl)).thenReturn(createdUrlDto);
        //Then
        ResponseEntity<UrlDto> response = urlController.postURL(request);
        assertEquals(HttpStatus.CREATED,response.getStatusCode());
        assertEquals(createdUrlDto,response.getBody());

        Mockito.verify(requestConverter).convertToEntity(request);
        Mockito.verify(urlService).create(createdUrl);
        Mockito.verify(dtoConverter).convertToDto(createdUrl);

        assertNotNull(createdUrl.getId());


    }
}