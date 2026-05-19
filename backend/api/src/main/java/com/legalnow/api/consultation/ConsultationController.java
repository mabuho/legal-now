package com.legalnow.api.consultation;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.legalnow.api.common.PageResponse;
import com.legalnow.api.consultation.dto.ConsultationResponse;
import com.legalnow.api.consultation.dto.CreateConsultationRequest;
import com.legalnow.api.consultation.dto.StatusTransitionRequest;
import com.legalnow.api.consultation.dto.UpdateConsultationRequest;
import com.legalnow.api.user.Role;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/consultations")
public class ConsultationController {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping
    public ResponseEntity<ConsultationResponse> create(@Valid @RequestBody CreateConsultationRequest req) {
        ConsultationResponse created = consultationService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/consultations/" + created.id())).body(created);
    }

    @GetMapping
    public PageResponse<ConsultationResponse> list(
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "role", required = false) String role,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        ConsultationStatus statusEnum = status == null ? null : ConsultationStatus.fromDb(status);
        Role asRole = role == null ? null : Role.fromString(role);
        Pageable pageable = PageRequest.of(
            Math.max(0, page),
            clampSize(size),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<ConsultationResponse> result = consultationService.listMine(statusEnum, asRole, pageable);
        return PageResponse.from(result, c -> c);
    }

    @GetMapping("/{id}")
    public ConsultationResponse get(@PathVariable UUID id) {
        return consultationService.getById(id);
    }

    @PatchMapping("/{id}")
    public ConsultationResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateConsultationRequest req) {
        return consultationService.update(id, req);
    }

    @PostMapping("/{id}/status")
    public ConsultationResponse transition(@PathVariable UUID id, @Valid @RequestBody StatusTransitionRequest req) {
        return consultationService.transition(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        consultationService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    private static int clampSize(int size) {
        if (size <= 0) return DEFAULT_PAGE_SIZE;
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
