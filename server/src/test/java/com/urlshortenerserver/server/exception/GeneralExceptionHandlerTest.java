package com.urlshortenerserver.server.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GeneralExceptionHandlerTest {

    // Minimal controller that only exists to trigger the handlers.
    @RestController
    static class FakeController {

        record Body(
                @NotBlank(message = "url must not be blank") String url,
                @Pattern(regexp = "^[A-Za-z0-9_-]{4,20}$",
                        message = "Code must be 4-20 characters") String code
        ) {}

        @GetMapping("/not-found")
        public String notFound() {
            throw new UrlNotFoundException("Url not found: XYZ");
        }

        @GetMapping("/conflict")
        public String conflict() {
            throw new CodeAlreadyExistsExceptiom("TAKEN1");
        }

        @PostMapping("/validate")
        public String validate(@Valid @RequestBody Body body) {
            return "ok";
        }
    }

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FakeController())
                .setControllerAdvice(new GeneralExceptionHandler())
                .build();
    }

    @Test
    void urlNotFound_returns404WithErrorMessage() throws Exception {
        mockMvc.perform(get("/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Url not found: XYZ"));
    }

    @Test
    void codeAlreadyExists_returns409WithErrorMessage() throws Exception {
        mockMvc.perform(get("/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", containsString("TAKEN1")));
    }

    @Test
    void invalidBody_returns400WithFieldErrors() throws Exception {
        String json = "{\"url\":\"https://example.com\",\"code\":\"ab\"}";

        mockMvc.perform(post("/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Code must be 4-20 characters"));
    }

    @Test
    void invalidBody_withMultipleErrors_returnsEveryField() throws Exception {
        String json = "{\"url\":\"\",\"code\":\"ab\"}";

        mockMvc.perform(post("/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").value("url must not be blank"))
                .andExpect(jsonPath("$.code").value("Code must be 4-20 characters"));
    }

    @Test
    void validBody_passesThroughWithoutHandler() throws Exception {
        String json = "{\"url\":\"https://example.com\",\"code\":\"mytest1\"}";

        mockMvc.perform(post("/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}