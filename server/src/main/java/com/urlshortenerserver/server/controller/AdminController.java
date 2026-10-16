package com.urlshortenerserver.server.controller;

import com.urlshortenerserver.server.request.filter.UrlFilter;
import com.urlshortenerserver.server.response.AdminStatResponse;
import com.urlshortenerserver.server.response.AdminUrlResponse;
import com.urlshortenerserver.server.response.PageResponse;
import com.urlshortenerserver.server.response.UrlClickStatResponse;
import com.urlshortenerserver.server.service.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@CrossOrigin
@RestController
@RequestMapping("/api/v2/admin")
@Tag(name = "Admin", description = "Administrative endpoints for managing URLs and viewing statistics. Requires an admin JWT.")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content),
        @ApiResponse(responseCode = "403", description = "Authenticated user is not an admin", content = @Content)
})
public class AdminController {
    private static final int MAX_PAGE_SIZE = 100;

    private final IAdminService adminService;

    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(
            summary = "List all URLs",
            description = "Returns a paginated list of all short URLs, newest first. "
                    + "Results can be narrowed using the filter parameters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of URLs returned"),
            @ApiResponse(responseCode = "400", description = "Invalid page or size", content = @Content)
    })
    @GetMapping("/urls/all")
    public ResponseEntity<PageResponse<AdminUrlResponse>> listUrls(
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (1-100)", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @ParameterObject @ModelAttribute UrlFilter filter) {

        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be 0 or greater");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size must be between 1 and " + MAX_PAGE_SIZE);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return ResponseEntity.ok(adminService.listUrls(filter, pageable));
    }

    @Operation(
            summary = "Restore a URL",
            description = "Restores a previously deactivated or deleted short URL, identified by its short code.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "URL restored", content = @Content),
            @ApiResponse(responseCode = "404", description = "No URL found for this code", content = @Content)
    })
    @PostMapping("/urls/{code}/restore")
    public ResponseEntity<Void> restoreUrl(
            @Parameter(description = "Short code of the URL", example = "aB3dE")
            @PathVariable String code) {
        adminService.restoreUrl(code);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get click statistics for a URL",
            description = "Returns click statistics and a paginated list of individual clicks for a short code, "
                    + "newest first. Optionally restrict the results to a time range.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Click statistics returned"),
            @ApiResponse(responseCode = "400", description = "Invalid page, size, or date format", content = @Content),
            @ApiResponse(responseCode = "404", description = "No URL found for this code", content = @Content)
    })
    @GetMapping("/{code}/clicks")
    public ResponseEntity<UrlClickStatResponse> getClickStats(
            @Parameter(description = "Short code of the URL", example = "aB3dE")
            @PathVariable String code,
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (1-100)", example = "50")
            @RequestParam(defaultValue = "50") int size,
            @Parameter(description = "Start of the time range (inclusive), ISO-8601 UTC", example = "2026-10-01T00:00:00Z")
            @RequestParam(required = false) Instant from,
            @Parameter(description = "End of the time range, ISO-8601 UTC", example = "2026-10-31T23:59:59Z")
            @RequestParam(required = false) Instant to) {

        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be 0 or greater");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size must be between 1 and " + MAX_PAGE_SIZE);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "clickedAt"));

        return ResponseEntity.ok(adminService.getUrlClicks(code, from, to, pageable));
    }

    @Operation(
            summary = "Get system statistics",
            description = "Returns aggregate statistics for the whole system.")
    @ApiResponse(responseCode = "200", description = "Statistics returned")
    @GetMapping("/stats")
    public ResponseEntity<AdminStatResponse> getSystemStats() {
        return ResponseEntity.ok(adminService.getSystemStats());
    }
}