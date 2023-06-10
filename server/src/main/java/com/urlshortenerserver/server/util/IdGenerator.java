package com.urlshortenerserver.server.util;

import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

    public Long generateID (Long id) {
        return id + 1;
    }
}
