package com.legalnow.api.lawyer;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.legalnow.api.common.SecurityUtils;
import com.legalnow.api.common.exception.ForbiddenException;
import com.legalnow.api.lawyer.dto.VerificationRequest;

import jakarta.validation.Valid;

@RestController
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/api/v1/lawyers/me/verification/request")
    public ResponseEntity<Void> requestVerification() {
        UUID lawyerId = SecurityUtils.currentUserId();
        verificationService.requestVerification(lawyerId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/api/v1/admin/lawyers/{id}/verify")
    public ResponseEntity<Void> verify(
        @PathVariable UUID id,
        @Valid @RequestBody(required = false) VerificationRequest req
    ) {
        if (!SecurityUtils.isAdmin()) {
            throw new ForbiddenException("Admin role required");
        }
        UUID adminId = SecurityUtils.currentUserId();
        String notes = req != null ? req.notes() : null;
        verificationService.approve(adminId, id, notes);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/admin/lawyers/{id}/reject")
    public ResponseEntity<Void> reject(
        @PathVariable UUID id,
        @Valid @RequestBody(required = false) VerificationRequest req
    ) {
        if (!SecurityUtils.isAdmin()) {
            throw new ForbiddenException("Admin role required");
        }
        UUID adminId = SecurityUtils.currentUserId();
        String notes = req != null ? req.notes() : null;
        verificationService.reject(adminId, id, notes);
        return ResponseEntity.ok().build();
    }
}
