package com.urlshortenerserver.server.service;

import com.urlshortenerserver.server.exception.CodeAlreadyExistsExceptiom;
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
import static org.mockito.ArgumentMatchers.any;

class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlService urlService;

    private RandomStringGenerator randomStringGenerator;

    private IdGenerator idGenerator;

    @Mock
    private CacheService cacheService;

    @BeforeEach
    void setUp(){
        idGenerator = Mockito.mock(IdGenerator.class);
        randomStringGenerator = Mockito.mock(RandomStringGenerator.class);
        Mockito.when(randomStringGenerator.generateRandomString()).thenReturn("NonExisting");
        MockitoAnnotations.openMocks(this);
        urlService = new UrlService(urlRepository,randomStringGenerator,idGenerator, cacheService);
    }

    @Test
    void getAllUrls() {
        //Given
        Url url1 = new Url(1L,"http://servicetest.com","test",false);
        Url url2 = new Url(2l,"http://hellothere.com","test2",false);
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
        Url url1 = new Url(1l,"http://example.com","TEST",false);
        String code = "TEST";
        //When
        Mockito.when(urlRepository.findAllByCodeAndDeletedFalse("TEST")).thenReturn(Optional.of(url1));
        //Then
        assertDoesNotThrow(() -> {
            Url urlResult = urlService.getUrlByCode(code);
            assertEquals(urlResult,url1);
        });
        //Verify
        Mockito.verify(urlRepository,Mockito.times(1)).findAllByCodeAndDeletedFalse(code);
    }

    @Test
    void getUrlByCodeException() {
        //Given
        String code = "NONEXISTENT";
        //When
        Mockito.when(urlRepository.findAllByCodeAndDeletedFalse("NONEXISTENT")).thenReturn(Optional.empty());
        //Then
        UrlNotFoundException notFoundException = assertThrows(UrlNotFoundException.class, () -> {
            urlService.getUrlByCode(code);
        });
        assertEquals("Url not found",notFoundException.getMessage());
        //Verify
        Mockito.verify(urlRepository,Mockito.times(1)).findAllByCodeAndDeletedFalse(code);
    }

    @Test
    void generateCode() {
        //Given
        String generatedCode = "generated";
        Url testUrl = new Url(1l,"http://helloThere.com","generated",false);
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
        Url url1 = new Url(1l,"http://example.com",null,false);
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


    private Url buildUrl(String code) {
        Url url = new Url();
        url.setUrl("https://example.com");
        url.setCode(code);
        return url;
    }

    @Test
    void createWithFreeCustomCodeSavesUppercasedCode() {
        // Given

        // When
        Mockito.when(urlRepository.existsByCode("MYTEST1")).thenReturn(false);
        Mockito.when(urlRepository.save(any(Url.class))).thenAnswer(inv -> inv.getArgument(0));

        Url result = urlService.create(buildUrl("mytest1"));

        assertEquals("MYTEST1", result.getCode());
        assertEquals("https://example.com", result.getUrl());
        Mockito.verify(urlRepository).existsByCode("MYTEST1");
        Mockito.verify(urlRepository).save(result);
    }

    @Test
    void createWithTakenCustomCodeThrowsAndDoesNotSave() {

        // Given

        // When
        Mockito.when(urlRepository.existsByCode("TAKEN1")).thenReturn(true);

        // Then
        CodeAlreadyExistsExceptiom ex = assertThrows(
                CodeAlreadyExistsExceptiom.class,
                () -> urlService.create(buildUrl("taken1"))
        );

        assertTrue(ex.getMessage().contains("TAKEN1"));
        Mockito.verify(urlRepository, Mockito.never()).save(any(Url.class));
    }

    @Test
    void createChecksDuplicateAfterUppercasing() {
        // Given

        // When
        Mockito.when(urlRepository.existsByCode("ABCD1")).thenReturn(true);
        // Then
        assertThrows(CodeAlreadyExistsExceptiom.class,
                () -> urlService.create(buildUrl("abcd1")));

        Mockito.verify(urlRepository).existsByCode("ABCD1");
        Mockito.verify(urlRepository, Mockito.never()).existsByCode("abcd1");
    }

    @Test
    void create_shouldGenerateCode_whenCodeIsNull() {
        // Given
        Url urlToCreate = new Url();
        urlToCreate.setUrl("https://example.com");
        urlToCreate.setCode(null);

        Url savedUrl = new Url();
        savedUrl.setId(1L);
        savedUrl.setUrl("https://example.com");
        savedUrl.setCode("ABCDE");
        savedUrl.setDeleted(false);

        Mockito.when(randomStringGenerator.generateRandomString()).thenReturn("abcde");
        Mockito.when(urlRepository.existsByCode("ABCDE")).thenReturn(false);
        Mockito.when(urlRepository.save(any(Url.class))).thenReturn(savedUrl);

        // When
        Url result = urlService.create(urlToCreate);

        // Then
        assertNotNull(result);
        assertEquals("ABCDE", result.getCode());
        assertEquals("https://example.com", result.getUrl());
        Mockito.verify(cacheService, Mockito.times(1)).cacheUrl("ABCDE", "https://example.com");
        Mockito.verify(urlRepository, Mockito.times(1)).save(any(Url.class));
    }

    @Test
    void getUrlByCode_shouldReturnCachedUrl_whenPresentInCache() throws Exception {
        // Given
        String code = "test";
        Mockito.when(cacheService.getUrlFromCache("TEST")).thenReturn("https://example.com");

        // When
        Url result = urlService.getUrlByCode(code);

        // Then
        assertNotNull(result);
        assertEquals("TEST", result.getCode());
        assertEquals("https://example.com", result.getUrl());

        Mockito.verify(cacheService,Mockito.times(1)).getUrlFromCache("TEST");
        Mockito.verify(urlRepository, Mockito.never()).findAllByCodeAndDeletedFalse(Mockito.anyString());
    }

    @Test
    void generateCode_shouldRetry_whenGeneratedCodeAlreadyExists() {
        // Given
        Url existing = new Url();
        existing.setId(1L);
        existing.setCode("duplicate");
        existing.setUrl("https://existing.com");
        existing.setDeleted(false);

        Mockito.when(randomStringGenerator.generateRandomString())
                .thenReturn("duplicate")
                .thenReturn("unique");
        Mockito.when(urlRepository.findAllByCode("duplicate")).thenReturn(Optional.of(existing));
        Mockito.when(urlRepository.findAllByCode("unique")).thenReturn(Optional.empty());

        // When
        String generatedCode = urlService.generateCode();

        // Then
        assertEquals("unique", generatedCode);
        Mockito.verify(randomStringGenerator, Mockito.times(2)).generateRandomString();
    }

    @Test
    void deleteUrl_shouldThrow_whenUrlDoesNotExist() {
        // Given
        Mockito.when(urlRepository.findAllByCode("NONEXISTENT")).thenReturn(Optional.empty());

        // When / Then
        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> urlService.deleteUrl("nonexistent")
        );

        assertEquals("Url not found", exception.getMessage());
        Mockito.verify(cacheService, Mockito.never()).invalidateCache(Mockito.anyString());
        Mockito.verify(urlRepository, Mockito.never()).save(any(Url.class));
    }

    @Test
    void deleteUrl_shouldMarkUrlDeleted_whenExists() throws Exception {
        // Given
        Url urlToDelete = new Url();
        urlToDelete.setId(1L);
        urlToDelete.setCode("TEST");
        urlToDelete.setUrl("https://example.com");
        urlToDelete.setDeleted(false);

        Mockito.when(urlRepository.findAllByCode("TEST")).thenReturn(Optional.of(urlToDelete));

        // When
        urlService.deleteUrl("test");

        // Then
        assertTrue(urlToDelete.isDeleted());
        Mockito.verify(urlRepository, Mockito.times(1)).save(urlToDelete);
        Mockito.verify(cacheService, Mockito.times(1)).invalidateCache("TEST");
    }

    @Test
    void generateID_shouldReturnNextId() {
        // Given
        Mockito.when(urlRepository.count()).thenReturn(5L);

        // When
        Long id = urlService.generateID();

        // Then
        assertEquals(6L, id);
        Mockito.verify(urlRepository, Mockito.times(1)).count();
    }
}