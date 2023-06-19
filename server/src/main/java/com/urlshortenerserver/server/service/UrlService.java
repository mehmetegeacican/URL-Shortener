package com.urlshortenerserver.server.service;


import com.urlshortenerserver.server.exception.CodeAlreadyExistsExceptiom;
import com.urlshortenerserver.server.exception.UrlNotFoundException;
import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.repository.UrlRepository;
import com.urlshortenerserver.server.util.IdGenerator;
import com.urlshortenerserver.server.util.RandomStringGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final RandomStringGenerator randomStringGenerator;

    private final IdGenerator idGenerator;

    public UrlService(UrlRepository urlRepository, RandomStringGenerator randomStringGenerator, IdGenerator idGenerator) {
        this.urlRepository = urlRepository;
        this.randomStringGenerator = randomStringGenerator;
        this.idGenerator = idGenerator;
    }

    public Url create(Url url) {
        if (url.getCode() == null || url.getCode().isEmpty()) {
            url.setCode(generateCode());
        }
        url.setCode(url.getCode().toUpperCase());
        return this.urlRepository.save(url);
    }


    public List<Url> getAllUrls() {
        return this.urlRepository.findAll();
    }

    public Url getUrlByCode(String code) throws Exception{
        return this.urlRepository.findAllByCode(code).orElseThrow(() -> new UrlNotFoundException("Url not found"));
    }

    public String generateCode(){
        String code = "";
        do {
            code = randomStringGenerator.generateRandomString();
        }while (urlRepository.findAllByCode(code).isPresent());
        return code;
    }

    public Long generateID(){
        Long repoSize = this.urlRepository.count();
        return (Long) repoSize + 1;
    }


}
