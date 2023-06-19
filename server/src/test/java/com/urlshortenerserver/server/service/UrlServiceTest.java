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

    @Mock
    private UrlService urlService;

    private RandomStringGenerator randomStringGenerator;

    private IdGenerator idGenerator;

    @BeforeEach
    void setUp(){
        idGenerator = Mockito.mock(IdGenerator.class);
        randomStringGenerator = Mockito.mock(RandomStringGenerator.class);
        Mockito.when(randomStringGenerator.generateRandomString()).thenReturn("NonExisting");
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

    @Test
    void generateCode() {
        //Given
        String generatedCode = "generated";
        Url testUrl = new Url(1l,"http://helloThere.com","generated");
        //When
        Mockito.when(urlRepository.findAllByCode(generatedCode)).thenReturn(Optional.of(testUrl));
        Mockito.when(urlRepository.findAllByCode("NonExisting")).thenReturn(Optional.empty());
        //Then
        assertEquals("NonExisting",urlService.generateCode());
        //Verify
        Mockito.verify(urlRepository,Mockito.atLeastOnce()).findAllByCode("NonExisting");
        Mockito.verify(randomStringGenerator,Mockito.atLeastOnce()).generateRandomString();
    }

    @Test
    void generateID() {
        //Given
        Long testCount = 1l;
        //When
        Mockito.when(urlRepository.count()).thenReturn(testCount);
        //Then
        assertEquals(urlService.generateID(),testCount + 1);
        //Verify
        Mockito.verify(urlRepository,Mockito.times(1)).count();
    }

    @Test
    void createwithNoCode() {
        //Given
        Url url1 = new Url(1l,"http://example.com",null);
        String generatedTestCode = "GENERATED";
        //When
        Mockito.when(randomStringGenerator.generateRandomString()).thenReturn(generatedTestCode);
        Mockito.when(urlRepository.save(url1)).thenReturn(url1);
        Url result = urlService.create(url1);
        //Then
        assertEquals(generatedTestCode,url1.getCode());
        assertEquals(result,url1);
        //Verify
        Mockito.verify(urlRepository,Mockito.times(1)).save(url1);
        Mockito.verify(randomStringGenerator,Mockito.times(1)).generateRandomString();
    }
}