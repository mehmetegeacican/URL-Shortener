package com.urlshortenerserver.server.controller;


import com.urlshortenerserver.server.response.AdminUrlResponse;
import com.urlshortenerserver.server.response.PageResponse;
import com.urlshortenerserver.server.service.IAdminService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@CrossOrigin
@RestController
@RequestMapping("/api/v2/admin")
public class AdminController {
    private static final int MAX_PAGE_SIZE = 100;

    private final IAdminService adminService;


    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/urls/all")
    public ResponseEntity<PageResponse<AdminUrlResponse>> listUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be 0 or greater");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size must be between 1 and " + MAX_PAGE_SIZE);
        }

        // Sorted by id for now; createdAt doesn't exist yet
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        return ResponseEntity.ok(adminService.listUrls(pageable));
    }
}

