package com.urlshortenerserver.server.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RandomStringGeneratorTest {

    private static final String ALLOWED = "ABCDEFGHIJKLMNPQRSTUVWXYZ0123456789";

    private RandomStringGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new RandomStringGenerator();
        ReflectionTestUtils.setField(generator, "codeLength", 5);
    }

    @Test
    void generateRandomString_hasConfiguredLength() {
        assertEquals(5, generator.generateRandomString().length());
    }

    @Test
    void generateRandomString_respectsDifferentLengths() {
        ReflectionTestUtils.setField(generator, "codeLength", 12);
        assertEquals(12, generator.generateRandomString().length());
    }

    @Test
    void generateRandomString_withZeroLength_returnsEmptyString() {
        ReflectionTestUtils.setField(generator, "codeLength", 0);
        assertEquals("", generator.generateRandomString());
    }

    @Test
    void generateRandomString_usesOnlyAllowedCharacters() {
        for (int i = 0; i < 200; i++) {
            String code = generator.generateRandomString();
            for (char c : code.toCharArray()) {
                assertTrue(ALLOWED.indexOf(c) >= 0,
                        "Unexpected character '" + c + "' in " + code);
            }
        }
    }

    @Test
    void generateRandomString_neverContainsLetterO() {
        for (int i = 0; i < 200; i++) {
            assertFalse(generator.generateRandomString().contains("O"));
        }
    }

    @Test
    void generateRandomString_isUppercase() {
        for (int i = 0; i < 100; i++) {
            String code = generator.generateRandomString();
            assertEquals(code.toUpperCase(), code);
        }
    }

    @Test
    void generateRandomString_producesVariedResults() {
        Set<String> results = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            results.add(generator.generateRandomString());
        }

        // 35^5 is about 52 million combinations, so 100 draws should be
        // almost all unique. A loose bound avoids flaky failures.
        assertTrue(results.size() > 90,
                "Expected varied codes but got only " + results.size() + " unique");
    }

    @Test
    void generateRandomString_matchesCodeValidationPattern() {
        for (int i = 0; i < 100; i++) {
            assertTrue(generator.generateRandomString().matches("^[A-Za-z0-9_-]{4,20}$"));
        }
    }


}