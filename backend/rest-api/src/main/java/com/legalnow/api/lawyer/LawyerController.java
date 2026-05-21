package com.legalnow.api.lawyer;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.legalnow.api.common.PageResponse;
import com.legalnow.api.lawyer.dto.CreateLawyerProfileRequest;
import com.legalnow.api.lawyer.dto.LawyerProfileResponse;
import com.legalnow.api.lawyer.dto.UpdateLawyerProfileRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/lawyers")
public class LawyerController {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final LawyerService lawyerService;

    public LawyerController(LawyerService lawyerService) {
        this.lawyerService = lawyerService;
    }

    @GetMapping
    public PageResponse<LawyerProfileResponse> list(
        @RequestParam(name = "specialization", required = false) String specialization,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(
            Math.max(0, page),
            clampSize(size),
            Sort.by(Sort.Direction.ASC, "createdAt")
        );
        Page<LawyerProfileResponse> result = lawyerService.listVerified(specialization, pageable);
        return PageResponse.from(result, x -> x);
    }

    @GetMapping("/me")
    public LawyerProfileResponse getMe() {
        return lawyerService.getMe();
    }

    @PostMapping("/me")
    public ResponseEntity<LawyerProfileResponse> createMe(@Valid @RequestBody CreateLawyerProfileRequest req) {
        LawyerProfileResponse created = lawyerService.createMe(req);
        return ResponseEntity.created(URI.create("/api/v1/lawyers/" + created.userId())).body(created);
    }

    @PatchMapping("/me")
    public LawyerProfileResponse updateMe(@Valid @RequestBody UpdateLawyerProfileRequest req) {
        return lawyerService.updateMe(req);
    }

    @GetMapping("/{id}")
    public LawyerProfileResponse get(@PathVariable UUID id) {
        return lawyerService.getById(id);
    }

    private static int clampSize(int size) {
        if (size <= 0) return DEFAULT_PAGE_SIZE;
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
