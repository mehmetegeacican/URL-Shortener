package com.urlshortenerserver.server.service;

import com.urlshortenerserver.server.exception.UrlNotFoundException;
import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.repository.UrlRepository;
import com.urlshortenerserver.server.util.IdGenerator;
import com.urlshortenerserver.server.util.RandomStringGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;

    private UrlService urlService;

    private RandomStringGenerator randomStringGenerator;

    private IdGenerator idGenerator;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        urlService = new UrlService(urlRepository,randomStringGenerator,idGenerator);
    }

    @Test
    void getAllUrls() {
        //Given
        Url url1 = new Url(1L,"http://servicetest.com","test");
        Url url2 = new Url(2l,"http://hellothere.com","test2");
        List<Url> testUrls = Arrays.asList(url1, url2);
        //When
        Mockito.when(urlRepository.findAll()).thenReturn(testUrls);
        //Then
        assertEquals(urlService.getAllUrls(),testUrls);
        //Verify
        Mockito.verify(urlRepository,Mockito.times(1)).findAll();
    }

    @Test
    void getUrlByCode() {
        //Given
        Url url1 = new Url(1l,"http://example.com","test");
        String code = "test";
        //When
        Mockito.when(urlRepository.findAllByCode("test")).thenReturn(Optional.of(url1));
        //Then
        assertDoesNotThrow(() -> {
            Url urlResult = urlService.getUrlByCode(code);
            assertEquals(urlResult,url1);
        });
        //Verify
        Mockito.verify(urlRepository,Mockito.times(1)).findAllByCode(code);
    }

    @Test
    void getUrlByCodeException() {
        //Given
        String code = "nonExistent";
        //When
        Mockito.when(urlRepository.findAllByCode("nonExistent")).thenReturn(Optional.empty());
        //Then
        UrlNotFoundException notFoundException = assertThrows(UrlNotFoundException.class, () -> {
            urlService.getUrlByCode(code);
        });
        assertEquals("Url not found",notFoundException.getMessage());
        //Verify
        Mockito.verify(urlRepository,Mockito.times(1)).findAllByCode(code);
    }
}